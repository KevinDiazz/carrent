package com.kevin.carrent.repository;

import com.kevin.carrent.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
}