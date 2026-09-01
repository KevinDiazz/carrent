package com.kevin.carrent.dto;

import java.math.BigDecimal;

public class CarAvailabilityResponse {

    private final Long carModelId;
    private final String brand;
    private final String model;
    private final String fuelType;
    private final String transmission;
    private final BigDecimal pricePerDay;
    private final Long availableCars;

    public CarAvailabilityResponse(
            Long carModelId,
            String brand,
            String model,
            String fuelType,
            String transmission,
            BigDecimal pricePerDay,
            Long availableCars) {

        this.carModelId = carModelId;
        this.brand = brand;
        this.model = model;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.pricePerDay = pricePerDay;
        this.availableCars = availableCars;
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

    public String getFuelType() {
        return fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public Long getAvailableCars() {
        return availableCars;
    }
}