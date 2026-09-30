package com.kevin.carrent.service;

import com.kevin.carrent.dto.ReservationCreateRequest;
import com.kevin.carrent.dto.ReservationResponse;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.entity.Reservations;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.CarStatus;
import com.kevin.carrent.enums.ReservationStatus;
import com.kevin.carrent.enums.Role;
import com.kevin.carrent.exception.*;
import com.kevin.carrent.mapper.ReservationMapper;
import com.kevin.carrent.repository.CarRepository;
import com.kevin.carrent.repository.ReservationRepository;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    ReservationService reservationService;

    @Test
    void shouldCreateReservationWhenHaveCarAvailable() {
        ReservationCreateRequest request = new ReservationCreateRequest();
        request.setCarModelId(1L);
        request.setOfficeId(1L);
        request.setFuelType("Gasoline");
        request.setTransmission("Manual");

        request.setStartDate(
                LocalDate.of(2026, 10, 10)
        );

        request.setEndDate(
                LocalDate.of(2026, 10, 15)
        );

        request.setPickupTime(
                LocalTime.of(10, 0)
        );

        request.setReturnTime(
                LocalTime.of(10, 0)
        );
        Car car = new Car();
        car.setPricePerDay(new BigDecimal("50.00"));

        when(carRepository.findAvailableCarsForReservation(
                request.getCarModelId(),
                request.getOfficeId(),
                request.getFuelType(),
                request.getTransmission(),
                request.getStartDate(),
                request.getEndDate()
        )).thenReturn(List.of(car));

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Kevin");

        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);


        Reservations savedReservation = new Reservations();

        when(reservationRepository.save(any(Reservations.class)))
                .thenReturn(savedReservation);

        when(authentication.getPrincipal())
                .thenReturn(user);

        ReservationResponse response = new ReservationResponse(
                1L,
                1L,
                "Kevin",
                1L,
                "1234ABC",
                "Toyota",
                "Corolla",
                1L,
                "Las Palmas",
                "Calle Mayor 1",
                "Las Palmas",
                "928123456",
                request.getStartDate(),
                request.getEndDate(),
                new BigDecimal("250.00"),
                request.getPickupTime(),
                request.getReturnTime(),
                ReservationStatus.CONFIRMED
        );

        when(reservationMapper.toResponse(savedReservation))
                .thenReturn(response);

        ReservationResponse result = reservationService.createReservation(request);

        assertEquals(response, result);

    }

    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        ReservationCreateRequest request = new ReservationCreateRequest();
        request.setCarModelId(1L);
        request.setOfficeId(1L);
        request.setFuelType("Gasoline");
        request.setTransmission("Manual");

        request.setStartDate(
                LocalDate.of(2026, 10, 18)
        );

        request.setEndDate(
                LocalDate.of(2026, 10, 15)
        );

        request.setPickupTime(
                LocalTime.of(10, 0)
        );

        request.setReturnTime(
                LocalTime.of(10, 0)
        );

        assertThrows(
                ReservationDateException.class,
                () -> reservationService.createReservation(request)
        );
        verify(carRepository, never()).findAvailableCarsForReservation(request.getCarModelId(),
                request.getOfficeId(),
                request.getFuelType(),
                request.getTransmission(),
                request.getStartDate(),
                request.getEndDate());

    }

    @Test
    void shouldThrowExceptionWhenStartDateEqualsEndDate() {
        ReservationCreateRequest request = new ReservationCreateRequest();
        request.setCarModelId(1L);
        request.setOfficeId(1L);
        request.setFuelType("Gasoline");
        request.setTransmission("Manual");

        request.setStartDate(
                LocalDate.of(2026, 10, 15)
        );

        request.setEndDate(
                LocalDate.of(2026, 10, 15)
        );

        request.setPickupTime(
                LocalTime.of(10, 0)
        );

        request.setReturnTime(
                LocalTime.of(10, 0)
        );

        assertThrows(
                ReservationDateException.class,
                () -> reservationService.createReservation(request)
        );
        verify(carRepository, never()).findAvailableCarsForReservation(request.getCarModelId(),
                request.getOfficeId(),
                request.getFuelType(),
                request.getTransmission(),
                request.getStartDate(),
                request.getEndDate());

    }

    @Test
    void shouldThrowExceptionWhenNoCarsAreAvailable() {
        ReservationCreateRequest request = new ReservationCreateRequest();
        request.setCarModelId(1L);
        request.setOfficeId(1L);
        request.setFuelType("Gasoline");
        request.setTransmission("Manual");

        request.setStartDate(
                LocalDate.of(2026, 10, 10)
        );

        request.setEndDate(
                LocalDate.of(2026, 10, 15)
        );

        request.setPickupTime(
                LocalTime.of(10, 0)
        );

        request.setReturnTime(
                LocalTime.of(10, 0)
        );
        when(carRepository.findAvailableCarsForReservation(
                request.getCarModelId(),
                request.getOfficeId(),
                request.getFuelType(),
                request.getTransmission(),
                request.getStartDate(),
                request.getEndDate()
        )).thenReturn(List.of());

        assertThrows(
                CarNotAvailableException.class,
                () -> reservationService.createReservation(request)
        );
    }

    @Test
    void shouldReturnRservationWithId() {
        Long id = 1L;
        User user = mock(User.class);

        when(user.getId()).thenReturn(id);
        when(user.getRole()).thenReturn(Role.USER);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Reservations savedReservation = new Reservations();
        savedReservation.setUser(user);

        ReservationResponse response = mock(ReservationResponse.class);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(savedReservation));

        when(reservationMapper.toResponse(any(Reservations.class)))
                .thenReturn(response);

        ReservationResponse result =
                reservationService.getReservationById(id);

        assertEquals(response, result);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotHaveAccess() {
        Long id = 1L;

        User authenticatedUser = mock(User.class);

        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getRole()).thenReturn(Role.USER);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        User reservationUser = mock(User.class);

        when(reservationUser.getId()).thenReturn(2L);

        Reservations savedReservation = new Reservations();
        savedReservation.setUser(reservationUser);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(savedReservation));

        assertThrows(
                ReservationAccessDeniedException.class,
                () -> reservationService.getReservationById(id)
        );
    }

    @Test
    void shouldReturnReservationWhenUserIsAdmin() {

        Long id = 1L;

        User user = mock(User.class);

        when(user.getRole()).thenReturn(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        User reservationUser = mock(User.class);

        Reservations savedReservation = new Reservations();
        savedReservation.setUser(reservationUser);

        ReservationResponse response = mock(ReservationResponse.class);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(savedReservation));

        when(reservationMapper.toResponse(savedReservation))
                .thenReturn(response);

        ReservationResponse result =
                reservationService.getReservationById(id);

        assertEquals(response, result);

        verify(reservationRepository).findById(id);
        verify(reservationMapper).toResponse(savedReservation);
    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExist() {

        Long id = 1L;

        when(reservationRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundException.class,
                () -> reservationService.getReservationById(id)
        );

        verify(reservationRepository).findById(id);

        verify(reservationMapper, never())
                .toResponse(any(Reservations.class));
    }

    @Test
    void shouldReturnAllReservationsWhenUserIsAdmin() {

        User user = mock(User.class);

        when(user.getRole()).thenReturn(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Reservations reservation1 = new Reservations();
        Reservations reservation2 = new Reservations();

        List<Reservations> reservations = List.of(reservation1, reservation2);

        when(reservationRepository.findAll()).thenReturn(reservations);
        ReservationResponse response1 = mock(ReservationResponse.class);
        ReservationResponse response2 = mock(ReservationResponse.class);

        when(reservationMapper.toResponse(reservation1))
                .thenReturn(response1);

        when(reservationMapper.toResponse(reservation2))
                .thenReturn(response2);
        List<ReservationResponse> result = reservationService.getReservations();
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
    }

    @Test
    void shouldReturnUserReservationsWhenUserIsNotAdmin() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Role.USER);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Reservations reservation1 = new Reservations();
        Reservations reservation2 = new Reservations();

        List<Reservations> reservations =
                List.of(reservation1, reservation2);

        when(reservationRepository.findByUserId(1L))
                .thenReturn(reservations);

        ReservationResponse response1 = mock(ReservationResponse.class);
        ReservationResponse response2 = mock(ReservationResponse.class);

        when(reservationMapper.toResponse(reservation1))
                .thenReturn(response1);

        when(reservationMapper.toResponse(reservation2))
                .thenReturn(response2);

        List<ReservationResponse> result =
                reservationService.getReservations();

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(reservationRepository).findByUserId(1L);

        verify(reservationRepository, never())
                .findAll();
    }
    @Test
    void shouldThrowExceptionWhenReservationDoesNotExistOnCancel() {

        Long id = 1L;

        when(reservationRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundException.class,
                () -> reservationService.cancelReservation(id)
        );

        verify(reservationRepository).findById(id);

        verify(reservationRepository, never())
                .save(any(Reservations.class));
    }
    @Test
    void shouldThrowExceptionWhenUserDoesNotHaveAccessToCancel() {

        Long id = 1L;

        User authenticatedUser = mock(User.class);

        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getRole()).thenReturn(Role.USER);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);


        User reservationUser = mock(User.class);

        when(reservationUser.getId()).thenReturn(2L);

        Reservations reservation = new Reservations();
        reservation.setUser(reservationUser);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationAccessDeniedException.class,
                () -> reservationService.cancelReservation(id)
        );

        verify(reservationRepository).findById(id);

        verify(reservationRepository, never())
                .save(any(Reservations.class));
    }
    @Test
    void shouldThrowExceptionWhenReservationIsAlreadyCancelled() {

        Long id = 1L;

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Role.USER);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Reservations reservation = new Reservations();

        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationAlreadyCancelledException.class,
                () -> reservationService.cancelReservation(id)
        );

        verify(reservationRepository).findById(id);

        verify(reservationRepository, never())
                .save(any(Reservations.class));
    }
    @Test
    void shouldCancelReservationSuccessfully() {

        Long id = 1L;

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Role.USER);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Reservations reservation = new Reservations();

        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(id);

        assertEquals(
                ReservationStatus.CANCELLED,
                reservation.getStatus()
        );

        verify(reservationRepository).findById(id);

        verify(reservationRepository).save(reservation);
    }
}
