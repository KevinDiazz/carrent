package com.kevin.carrent.controller;

import com.kevin.carrent.dto.ReservationCreateRequest;
import com.kevin.carrent.dto.ReservationResponse;
import com.kevin.carrent.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations() {

        return ResponseEntity.ok(
                reservationService.getReservations()
        );
    }
}