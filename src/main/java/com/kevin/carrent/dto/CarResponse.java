package com.kevin.carrent.dto;

import com.kevin.carrent.enums.CarStatus;

import java.math.BigDecimal;

public class CarResponse {

    private final Long id;
    private final Long carModelId;
    private final String brand;
    private final String model;
    private final Long officeId;
    private final String officeName;
    private final Integer year;
    private final String licensePlate;
    private final String fuelType;
    private final String transmission;
    private final BigDecimal pricePerDay;
    private final CarStatus status;

    public CarResponse(
            Long id,
            Long carModelId,
            String brand,
            String model,
            Long officeId,
            String officeName,
            Integer year,
            String licensePlate,
            String fuelType,
            String transmission,
            BigDecimal pricePerDay,
            CarStatus status) {

        this.id = id;
        this.carModelId = carModelId;
        this.brand = brand;
        this.model = model;
        this.officeId = officeId;
        this.officeName = officeName;
        this.year = year;
        this.licensePlate = licensePlate;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCarModelId() {
        return carModelId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Integer getYear() {
        return year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public CarStatus getStatus() {
        return status;
    }
}