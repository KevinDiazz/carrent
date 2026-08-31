package com.kevin.carrent.specification;

import com.kevin.carrent.dto.CarFilterRequest;
import com.kevin.carrent.entity.Car;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {

    public static Specification<Car> filter(
            CarFilterRequest filter) {

        return (root, query, criteriaBuilder) -> {

            return criteriaBuilder.and(

                    filter.getOfficeId() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.equal(
                            root.get("office").get("id"),
                            filter.getOfficeId()
                    ),

                    filter.getCarModelId() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.equal(
                            root.get("carModel").get("id"),
                            filter.getCarModelId()
                    ),

                    filter.getMinPrice() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.greaterThanOrEqualTo(
                            root.get("pricePerDay"),
                            filter.getMinPrice()
                    ),

                    filter.getMaxPrice() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.lessThanOrEqualTo(
                            root.get("pricePerDay"),
                            filter.getMaxPrice()
                    ),

                    filter.getFuelType() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.equal(
                            root.get("fuelType"),
                            filter.getFuelType()
                    ),

                    filter.getTransmission() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.equal(
                            root.get("transmission"),
                            filter.getTransmission()
                    ),

                    filter.getStatus() == null
                            ? criteriaBuilder.conjunction()
                            : criteriaBuilder.equal(
                            root.get("status"),
                            filter.getStatus()
                    )
            );
        };
    }
}