package com.kevin.carrent.dto;

import jakarta.validation.constraints.NotBlank;

public class CarModelUpdateRequest {

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    public CarModelUpdateRequest() {
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}