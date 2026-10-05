package com.kevin.carrent.security;

import com.kevin.carrent.dto.CarFilterRequest;
import com.kevin.carrent.repository.UserRepository;
import com.kevin.carrent.service.CarModelService;
import com.kevin.carrent.service.CarService;
import com.kevin.carrent.service.JwtService;
import com.kevin.carrent.service.ReservationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.kevin.carrent.dto.CarModelCreateRequest;
import com.kevin.carrent.dto.CarModelResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.Role;

import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private CarModelService carModelService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void shouldReturn401WhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(
                        get("/reservations")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "kevin", roles = "USER")
    void shouldAllowAuthenticatedUser() throws Exception {

        when(reservationService.getReservations())
                .thenReturn(List.of());

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "kevin", roles = "USER")
    void shouldReturn403WhenUserAccessesAdminEndpoint() throws Exception {

        mockMvc.perform(get("/cars"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAllowAdminToAccessCars() throws Exception {

        when(carService.getCars(any(CarFilterRequest.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "kevin", roles = "USER")
    void shouldAllowUserToAccessCarModels() throws Exception {

        when(carModelService.getAllCarModels())
                .thenReturn(List.of());

        mockMvc.perform(get("/car-models"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "kevin", roles = "USER")
    void shouldReturn403WhenUserCreatesCarModel() throws Exception {

        mockMvc.perform(
                        post("/car-models")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "brand": "Toyota",
                                                "model": "Corolla"
                                            }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAllowAdminToCreateCarModel() throws Exception {

        CarModelResponse response = mock(CarModelResponse.class);

        when(carModelService.createCarModel(any(CarModelCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/car-models")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "brand": "Toyota",
                                                "model": "Corolla"
                                            }
                                        """)
                )
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn403WhenCsrfTokenIsMissing() throws Exception {

        mockMvc.perform(
                        post("/car-models")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "brand": "Toyota",
                                                "model": "Corolla"
                                            }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowRequestWithValidJwt() throws Exception {

        User user = mock(User.class);

        when(user.getEmail()).thenReturn("kevin@test.com");
        when(user.getRole()).thenReturn(Role.USER);

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("kevin@test.com");

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("valid-token", user))
                .thenReturn(true);

        when(reservationService.getReservations())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/reservations")
                                .header("Authorization", "Bearer valid-token")
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenJwtIsInvalid() throws Exception {

        when(jwtService.extractEmail("invalid-token"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                        get("/reservations")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenJwtUserDoesNotExist() throws Exception {

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("unknown@test.com");

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/reservations")
                                .header("Authorization", "Bearer valid-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenJwtDoesNotBelongToUser() throws Exception {

        User user = mock(User.class);

        when(user.getEmail()).thenReturn("other@test.com");

        when(jwtService.extractEmail("invalid-user-token"))
                .thenReturn("kevin@test.com");

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("invalid-user-token", user))
                .thenReturn(false);

        mockMvc.perform(
                        get("/reservations")
                                .header("Authorization", "Bearer invalid-user-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRequestWithValidJwtFromCookie() throws Exception {

        User user = mock(User.class);

        when(user.getEmail()).thenReturn("kevin@test.com");
        when(user.getRole()).thenReturn(Role.USER);

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("kevin@test.com");

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("valid-token", user))
                .thenReturn(true);

        when(reservationService.getReservations())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/reservations")
                                .cookie(new Cookie("access_token", "valid-token"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenJwtIsMissing() throws Exception {

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenUserJwtAccessesAdminEndpoint() throws Exception {

        User user = mock(User.class);

        when(user.getEmail()).thenReturn("kevin@test.com");
        when(user.getRole()).thenReturn(Role.USER);

        when(jwtService.extractEmail("user-token"))
                .thenReturn("kevin@test.com");

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("user-token", user))
                .thenReturn(true);

        mockMvc.perform(
                        get("/cars")
                                .header("Authorization", "Bearer user-token")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminJwtAccessToCars() throws Exception {

        User user = mock(User.class);

        when(user.getEmail()).thenReturn("admin@test.com");
        when(user.getRole()).thenReturn(Role.ADMIN);

        when(jwtService.extractEmail("admin-token"))
                .thenReturn("admin@test.com");

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("admin-token", user))
                .thenReturn(true);

        when(carService.getCars(any(CarFilterRequest.class)))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/cars")
                                .header("Authorization", "Bearer admin-token")
                )
                .andExpect(status().isOk());
    }
}
