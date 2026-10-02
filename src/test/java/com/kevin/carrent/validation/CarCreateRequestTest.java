package com.kevin.carrent.validation;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.enums.CarStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CarCreateRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void shouldBeValidWhenRequestIsCorrect() {

        CarCreateRequest request = new CarCreateRequest();

        request.setCarModelId(1L);
        request.setOfficeId(2L);
        request.setYear(2024);
        request.setLicensePlate("1234ABC");
        request.setFuelType("PETROL");
        request.setTransmission("MANUAL");
        request.setPricePerDay(new BigDecimal("50.00"));
        request.setStatus(CarStatus.AVAILABLE);

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenLicensePlateIsBlank() {

        // Arrange
        CarCreateRequest request = new CarCreateRequest();
        request.setCarModelId(1L);
        request.setOfficeId(2L);
        request.setYear(2024);
        request.setLicensePlate("");
        request.setFuelType("PETROL");
        request.setTransmission("MANUAL");
        request.setPricePerDay(new BigDecimal("50.00"));
        request.setStatus(CarStatus.AVAILABLE);

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("licensePlate")));
    }
    @Test
    void shouldBeInvalidWhenPricePerDayIsNotPositive() {

        // Arrange
        CarCreateRequest request = new CarCreateRequest();

        request.setCarModelId(1L);
        request.setOfficeId(2L);
        request.setYear(2024);
        request.setLicensePlate("1234ABC");
        request.setFuelType("PETROL");
        request.setTransmission("MANUAL");
        request.setPricePerDay(new BigDecimal("0"));
        request.setStatus(CarStatus.AVAILABLE);

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pricePerDay")));
    }
    @Test
    void shouldReturnMultipleViolationsWhenRequestIsInvalid() {

        // Arrange
        CarCreateRequest request = new CarCreateRequest();

        request.setCarModelId(null);
        request.setOfficeId(null);
        request.setYear(null);
        request.setLicensePlate("");
        request.setFuelType("");
        request.setTransmission("");
        request.setPricePerDay(BigDecimal.ZERO);
        request.setStatus(null);

        // Act
        var violations = validator.validate(request);

        // Assert
        assertEquals(8, violations.size());
    }
}
