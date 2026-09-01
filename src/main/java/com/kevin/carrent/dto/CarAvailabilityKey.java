package com.kevin.carrent.dto;

import java.math.BigDecimal;

public record CarAvailabilityKey(
        Long carModelId,
        String fuelType,
        String transmission,
        BigDecimal pricePerDay
) {
}