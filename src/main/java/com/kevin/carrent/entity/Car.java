package com.kevin.carrent.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;
    @Column(nullable = false)
    private Integer year;
    @Column(nullable = false, unique = true)
    private String licensePlate;
    @Column(nullable = false)
    private String fuelType;
    @Column(nullable = false)
    private String transmission;
    @Column(nullable = false)
    private BigDecimal pricePerDay;
    @Column(nullable = false)
    private String status;
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    public Car() {
    }

    public Long getId() {
        return id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }
}
