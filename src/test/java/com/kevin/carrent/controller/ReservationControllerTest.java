package com.kevin.carrent.controller;


import com.kevin.carrent.dto.ReservationCreateRequest;
import com.kevin.carrent.dto.ReservationResponse;
import com.kevin.carrent.enums.ReservationStatus;
import com.kevin.carrent.exception.ReservationNotFoundException;
import com.kevin.carrent.repository.UserRepository;
import com.kevin.carrent.service.JwtService;
import com.kevin.carrent.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturnReservations() throws Exception {
        ReservationResponse reservation1 = new ReservationResponse(
                1L,
                10L,
                "Kevin",
                100L,
                "1234ABC",
                "Seat",
                "Ibiza",
                1L,
                "Oficina Las Palmas",
                "Calle Mayor 10",
                "Las Palmas",
                "928123456",
                LocalDate.of(2027, 10, 1),
                LocalDate.of(2027, 10, 5),
                new BigDecimal("150.00"),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                ReservationStatus.CONFIRMED
        );

        ReservationResponse reservation2 = new ReservationResponse(
                2L,
                11L,
                "Juan",
                101L,
                "5678DEF",
                "Volkswagen",
                "Golf",
                2L,
                "Oficina Maspalomas",
                "Avenida de Canarias 20",
                "Maspalomas",
                "928654321",
                LocalDate.of(2027, 10, 10),
                LocalDate.of(2027, 10, 15),
                new BigDecimal("300.00"),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                ReservationStatus.CONFIRMED
        );

        when(reservationService.getReservations()).thenReturn(List.of(reservation1, reservation2));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].model").value("Ibiza"))
                .andExpect(jsonPath("$[0].totalPrice").value(150.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].model").value("Golf"))
                .andExpect(jsonPath("$[1].totalPrice").value(300.00));

        verify(reservationService).getReservations();
    }

    @Test
    void shouldReturnReservationById() throws Exception {
        ReservationResponse reservation2 = new ReservationResponse(
                2L,
                11L,
                "Juan",
                101L,
                "5678DEF",
                "Volkswagen",
                "Golf",
                2L,
                "Oficina Maspalomas",
                "Avenida de Canarias 20",
                "Maspalomas",
                "928654321",
                LocalDate.of(2027, 10, 10),
                LocalDate.of(2027, 10, 15),
                new BigDecimal("300.00"),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                ReservationStatus.CONFIRMED
        );

        when(reservationService.getReservationById(2L)).thenReturn(reservation2);

        mockMvc.perform(
                        get("/reservations/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.model").value("Golf"))
                .andExpect(jsonPath("$.totalPrice").value(300.00));


        verify(reservationService).getReservationById(2L);

    }

    @Test
    void shouldReturnNotFoundWhenReservationDoesNotExist() throws Exception {
        when(reservationService.getReservationById(999L)).thenThrow(new ReservationNotFoundException(" \"Reservation with id \" + id + \" not found\""));

        mockMvc.perform(
                        get("/reservations/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));

        verify(reservationService).getReservationById(999L);
    }

    @Test
    void shouldCreateReservation() throws Exception {
        ReservationResponse reservationResponse = new ReservationResponse(
                1L,
                10L,
                "Kevin",
                100L,
                "1234ABC",
                "Seat",
                "Ibiza",
                2L,
                "Oficina Las Palmas",
                "Calle Mayor 10",
                "Las Palmas",
                "928123456",
                LocalDate.of(2027, 10, 2),
                LocalDate.of(2027, 10, 5),
                new BigDecimal("150.00"),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                ReservationStatus.CONFIRMED
        );

        when(reservationService.createReservation(any(ReservationCreateRequest.class)))
                .thenReturn(reservationResponse);
        // Act + Assert
        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "carModelId": 1,
                                                "officeId": 2,
                                                "fuelType": "PETROL",
                                                "transmission": "MANUAL",
                                                "startDate": "2027-10-02",
                                                "endDate": "2027-10-05",
                                                "pickupTime": "10:00:00",
                                                "returnTime": "18:00:00"
                                            }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.userName").value("Kevin"))
                .andExpect(jsonPath("$.carId").value(100))
                .andExpect(jsonPath("$.model").value("Ibiza"))
                .andExpect(jsonPath("$.totalPrice").value(150.00))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        // Verify
        verify(reservationService).createReservation(any(ReservationCreateRequest.class));

    }

    @Test
    void shouldReturnBadRequestWhenCarModelIdIsMissing() throws Exception {

        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "officeId": 2,
                                                "fuelType": "PETROL",
                                                "transmission": "MANUAL",
                                                "startDate": "2027-10-02",
                                                "endDate": "2027-10-05",
                                                "pickupTime": "10:00:00",
                                                "returnTime": "18:00:00"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenStartDateIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "carModelId": 1,
                                                "officeId": 2,
                                                "fuelType": "PETROL",
                                                "transmission": "MANUAL",
                                                "startDate": "2026-09-20",
                                                "endDate": "2027-10-05",
                                                "pickupTime": "10:00:00",
                                                "returnTime": "18:00:00"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturnBadRequestWhenEndDateIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "carModelId": 1,
                                                "officeId": 2,
                                                "fuelType": "PETROL",
                                                "transmission": "MANUAL",
                                                "startDate": "2026-09-20",
                                                "endDate": "2026-10-05",
                                                "pickupTime": "10:00:00",
                                                "returnTime": "18:00:00"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOfficeIdIsMissing() throws Exception {

        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "carModelId": 1,
                                                "fuelType": "PETROL",
                                                "transmission": "MANUAL",
                                                "startDate": "2027-10-02",
                                                "endDate": "2027-10-05",
                                                "pickupTime": "10:00:00",
                                                "returnTime": "18:00:00"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldNotCallServiceWhenRequestIsInvalid() throws Exception {

        // Act + Assert
        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "officeId": 2,
                                                "fuelType": "PETROL",
                                                "transmission": "MANUAL",
                                                "startDate": "2027-10-02",
                                                "endDate": "2027-10-05",
                                                "pickupTime": "10:00:00",
                                                "returnTime": "18:00:00"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());

        // Verify
        verify(reservationService, never()).createReservation(any(ReservationCreateRequest.class));
    }

    @Test
    void shouldCancelReservation() throws Exception {

        // Act + Assert
        mockMvc.perform(
                        delete("/reservations/1")
                )
                .andExpect(status().isNoContent());

        // Verify
        verify(reservationService).cancelReservation(1L);
    }
}
