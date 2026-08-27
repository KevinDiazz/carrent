package com.kevin.carrent.service;

import com.kevin.carrent.dto.ReservationCreateRequest;
import com.kevin.carrent.dto.ReservationResponse;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.entity.Reservations;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.Role;
import com.kevin.carrent.exception.CarNotFoundException;
import com.kevin.carrent.exception.ReservationDateException;
import com.kevin.carrent.exception.CarNotAvailableException;
import com.kevin.carrent.mapper.ReservationMapper;
import com.kevin.carrent.repository.CarRepository;
import com.kevin.carrent.repository.ReservationRepository;
import com.kevin.carrent.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(
            ReservationRepository reservationRepository,
            CarRepository carRepository,
            UserRepository userRepository,
            ReservationMapper reservationMapper) {

        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.reservationMapper = reservationMapper;
    }

    public ReservationResponse createReservation(
            ReservationCreateRequest request) {

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new ReservationDateException(
                    "Start date must be before end date"
            );
        }

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car with id " + request.getCarId()
                                        + " not found"
                        ));

        boolean carIsReserved =
                reservationRepository
                        .existsByCarIdAndStartDateLessThanAndEndDateGreaterThan(
                                car.getId(),
                                request.getEndDate(),
                                request.getStartDate()
                        );

        if (carIsReserved) {
            throw new CarNotAvailableException(
                    "Car with id " + car.getId()
                            + " is not available for the selected dates"
            );
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();


        long days = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        );

        BigDecimal totalPrice =
                car.getPricePerDay()
                        .multiply(BigDecimal.valueOf(days));

        Reservations reservation = new Reservations();

        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setPickupTime(request.getPickupTime());
        reservation.setReturnTime(request.getReturnTime());
        reservation.setTotalPrice(totalPrice);
        Reservations savedReservation =
                reservationRepository.save(reservation);

        return reservationMapper.toResponse(savedReservation);
    }

    public List<ReservationResponse> getReservations() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        List<Reservations> reservations;

        if (user.getRole() == Role.ADMIN) {
            reservations = reservationRepository.findAll();
        } else {
            reservations = reservationRepository.findByUserId(user.getId());
        }

        return reservations.stream()
                .map(reservationMapper::toResponse)
                .toList();
    }
}