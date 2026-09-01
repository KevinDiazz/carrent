package com.kevin.carrent.repository;

import com.kevin.carrent.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor

        <Car> {
    boolean existsByLicensePlate(String licensePlate);

    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

    boolean existsByCarModelId(Long carModelId);

    boolean existsByOfficeId(Long officeId);

    @Query("""
            SELECT c
            FROM Car c
            WHERE c.status = com.kevin.carrent.enums.CarStatus.AVAILABLE
            AND NOT EXISTS (
                SELECT r
                FROM Reservations r
                WHERE r.car.id = c.id
                AND r.status = com.kevin.carrent.enums.ReservationStatus.CONFIRMED
                AND r.startDate < :endDate
                AND r.endDate > :startDate
            )
            """)
    List<Car> findAvailableCars(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
