package com.kevin.carrent.repository;

import com.kevin.carrent.entity.Reservations;
import com.kevin.carrent.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservations, Long> {

    boolean existsByCarIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
            Long carId,
            ReservationStatus status,
            LocalDate endDate,
            LocalDate startDate
    );

    List<Reservations> findByUserId(Long userId);
}