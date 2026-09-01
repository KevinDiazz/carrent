package com.kevin.carrent.specification;

import com.kevin.carrent.dto.CarFilterRequest;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.entity.Reservations;
import com.kevin.carrent.enums.CarStatus;
import com.kevin.carrent.enums.ReservationStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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

    public static Specification<Car> availableCars(
            LocalDate startDate,
            LocalDate endDate,
            Long officeId) {

        return (root, query, criteriaBuilder) -> {

            Subquery<Long> subquery = query.subquery(Long.class);

            Root<Reservations> reservationRoot =
                    subquery.from(Reservations.class);

            subquery.select(reservationRoot.get("id"));

            subquery.where(
                    criteriaBuilder.equal(
                            reservationRoot.get("car").get("id"),
                            root.get("id")
                    ),

                    criteriaBuilder.equal(
                            reservationRoot.get("status"),
                            ReservationStatus.CONFIRMED
                    ),

                    criteriaBuilder.lessThan(
                            reservationRoot.get("startDate"),
                            endDate
                    ),

                    criteriaBuilder.greaterThan(
                            reservationRoot.get("endDate"),
                            startDate
                    )
            );

            return criteriaBuilder.and(

                    // El coche debe estar AVAILABLE
                    criteriaBuilder.equal(
                            root.get("status"),
                            CarStatus.AVAILABLE
                    ),

                    // No puede existir una reserva confirmada que se solape
                    criteriaBuilder.not(
                            criteriaBuilder.exists(subquery)
                    ),

                    // La búsqueda debe pertenecer a la oficina indicada
                    criteriaBuilder.equal(
                            root.get("office").get("id"),
                            officeId
                    )
            );
        };
    }
}