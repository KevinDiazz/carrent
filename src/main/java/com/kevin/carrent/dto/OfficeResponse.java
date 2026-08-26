package com.kevin.carrent.dto;

public class OfficeResponse {

    private final Long id;
    private final String name;
    private final String address;
    private final String city;
    private final String phone;

    public OfficeResponse(
            Long id,
            String name,
            String address,
            String city,
            String phone) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }
}