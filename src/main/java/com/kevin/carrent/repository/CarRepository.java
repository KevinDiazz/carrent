package com.kevin.carrent.repository;

import com.kevin.carrent.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor

        <Car> {
    boolean existsByLicensePlate(String licensePlate);

    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

    boolean existsByCarModelId(Long carModelId);

    boolean existsByOfficeId(Long officeId);
}
