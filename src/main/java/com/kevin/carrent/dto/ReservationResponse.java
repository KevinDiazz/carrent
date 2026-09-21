package com.kevin.carrent.dto;

import com.kevin.carrent.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationResponse {

    private final Long id;

    private final Long userId;
    private final String userName;

    private final Long carId;
    private final String licensePlate;
    private final String brand;
    private final String model;

    private final Long officeId;
    private final String officeName;
    private final String officeAddress;
    private final String officeCity;
    private final String officePhone;

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalPrice;
    private final LocalTime pickupTime;
    private final LocalTime returnTime;
    private final ReservationStatus status;

    public ReservationResponse(
            Long id,
            Long userId,
            String userName,
            Long carId,
            String licensePlate,
            String brand,
            String model,
            Long officeId,
            String officeName,
            String officeAddress,
            String officeCity,
            String officePhone,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalPrice,
            LocalTime pickupTime,
            LocalTime returnTime,
            ReservationStatus status) {

        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.carId = carId;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.officeId = officeId;
        this.officeName = officeName;
        this.officeAddress = officeAddress;
        this.officeCity = officeCity;
        this.officePhone = officePhone;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.pickupTime = pickupTime;
        this.returnTime = returnTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getCarId() {
        return carId;
    }

    public String getLicensePlate() {
        return licensePlate;
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

    public String getOfficeAddress() {
        return officeAddress;
    }

    public String getOfficeCity() {
        return officeCity;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public LocalTime getReturnTime() {
        return returnTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}