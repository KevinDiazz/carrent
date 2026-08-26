package com.kevin.carrent.repository;

import com.kevin.carrent.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, Long> {
}