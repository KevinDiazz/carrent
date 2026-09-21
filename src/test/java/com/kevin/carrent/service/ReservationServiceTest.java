package com.kevin.carrent.service;

import com.kevin.carrent.dto.ReservationCreateRequest;
import com.kevin.carrent.dto.ReservationResponse;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.entity.Reservations;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.CarStatus;
import com.kevin.carrent.enums.ReservationStatus;
import com.kevin.carrent.enums.Role;
import com.kevin.carrent.exception.CarNotAvailableException;
import com.kevin.carrent.exception.ReservationAccessDeniedException;
import com.kevin.carrent.exception.ReservationAlreadyCancelledException;
import com.kevin.carrent.exception.ReservationDateException;
import com.kevin.carrent.exception.ReservationNotFoundException;
import com.kevin.carrent.mapper.ReservationMapper;
import com.kevin.carrent.repository.CarRepository;
import com.kevin.carrent.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    private User loggedInUser;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );
    }

    private User buildUser(Long id, Role role) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setName("Test User");
        user.setEmail("user" + id + "@test.com");
        user.setPassword("encoded");
        user.setRole(role);
        return user;
    }

    private ReservationCreateRequest buildRequest(LocalDate start, LocalDate end) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        request.setCarModelId(1L);
        request.setOfficeId(1L);
        request.setFuelType("PETROL");
        request.setTransmission("MANUAL");
        request.setStartDate(start);
        request.setEndDate(end);
        request.setPickupTime(LocalTime.of(9, 0));
        request.setReturnTime(LocalTime.of(18, 0));
        return request;
    }

    @Test
    void createReservation_shouldThrow_whenStartDateIsNotBeforeEndDate() {
        ReservationCreateRequest request = buildRequest(
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(1)
        );

        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(ReservationDateException.class);

        verifyNoCarLookupOrSave();
    }

    private void verifyNoCarLookupOrSave() {
        verify(carRepository, never()).findAvailableCarsForReservation(
                any(), any(), any(), any(), any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_shouldThrow_whenNoCarsAvailable() {
        ReservationCreateRequest request = buildRequest(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        when(carRepository.findAvailableCarsForReservation(
                anyLong(), anyLong(), anyString(), anyString(), any(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(CarNotAvailableException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_shouldComputeTotalPriceAndSaveAsConfirmed() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(4);
        ReservationCreateRequest request = buildRequest(start, end);

        Car car = new Car();
        car.setPricePerDay(new BigDecimal("50.00"));

        loggedInUser = buildUser(1L, Role.USER);
        authenticateAs(loggedInUser);

        when(carRepository.findAvailableCarsForReservation(
                anyLong(), anyLong(), anyString(), anyString(), any(), any()))
                .thenReturn(List.of(car));

        when(reservationRepository.save(any(Reservations.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(reservationMapper.toResponse(any(Reservations.class)))
                .thenReturn(new ReservationResponse(
                        1L, 1L, "Test User", 1L, "AB1234CD",
                        "Toyota", "Yaris", 1L, "Main Office", "Test St", "Test City", "000000000",
                        start, end, new BigDecimal("150.00"),
                        LocalTime.of(9, 0), LocalTime.of(18, 0),
                        ReservationStatus.CONFIRMED
                ));

        reservationService.createReservation(request);

        ArgumentCaptor<Reservations> captor = ArgumentCaptor.forClass(Reservations.class);
        verify(reservationRepository).save(captor.capture());

        Reservations saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(saved.getUser()).isEqualTo(loggedInUser);
        assertThat(saved.getCar()).isEqualTo(car);
        // 3 days between start and end at 50.00/day
        assertThat(saved.getTotalPrice()).isEqualByComparingTo("150.00");
    }

    @Test
    void getReservationById_shouldThrow_whenReservationDoesNotExist() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservationById(99L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    void getReservationById_shouldThrow_whenUserIsNotOwnerNorAdmin() {
        User owner = buildUser(1L, Role.USER);
        User otherUser = buildUser(2L, Role.USER);

        Reservations reservation = new Reservations();
        reservation.setUser(owner);

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        authenticateAs(otherUser);

        assertThatThrownBy(() -> reservationService.getReservationById(10L))
                .isInstanceOf(ReservationAccessDeniedException.class);
    }

    @Test
    void cancelReservation_shouldThrow_whenAlreadyCancelled() {
        User owner = buildUser(1L, Role.USER);

        Reservations reservation = new Reservations();
        reservation.setUser(owner);
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        authenticateAs(owner);

        assertThatThrownBy(() -> reservationService.cancelReservation(5L))
                .isInstanceOf(ReservationAlreadyCancelledException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservation_shouldSetStatusCancelled_whenOwnerCancelsConfirmedReservation() {
        User owner = buildUser(1L, Role.USER);

        Reservations reservation = new Reservations();
        reservation.setUser(owner);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        authenticateAs(owner);

        reservationService.cancelReservation(5L);

        ArgumentCaptor<Reservations> captor = ArgumentCaptor.forClass(Reservations.class);
        verify(reservationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }
}
