package com.kevin.carrent.service;

import com.kevin.carrent.dto.LoginRequest;
import com.kevin.carrent.dto.LoginResult;
import com.kevin.carrent.dto.RegisterRequest;
import com.kevin.carrent.dto.RegisterResponse;
import com.kevin.carrent.entity.RefreshToken;
import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.Role;
import com.kevin.carrent.exception.EmailAlreadyExistsException;
import com.kevin.carrent.exception.InvalidCredentialsException;
import com.kevin.carrent.mapper.UserMapper;
import com.kevin.carrent.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserService userService;

    private User buildStoredUser(String email, String encodedPassword) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        return user;
    }

    @Test
    void registerUser_shouldThrow_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Kevin");
        request.setEmail("kevin@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(buildStoredUser("kevin@test.com", "hash")));

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_shouldEncodePasswordAndAssignUserRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Kevin");
        request.setEmail("kevin@test.com");
        request.setPassword("plainPassword");

        User mappedUser = new User();
        mappedUser.setName("Kevin");
        mappedUser.setEmail("kevin@test.com");

        when(userRepository.findByEmail("kevin@test.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class)))
                .thenReturn(new RegisterResponse("Kevin", "kevin@test.com", Role.USER));

        userService.registerUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void login_shouldThrow_whenEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_shouldThrow_whenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("kevin@test.com");
        request.setPassword("wrongPassword");

        User storedUser = buildStoredUser("kevin@test.com", "encodedPassword");

        when(userRepository.findByEmail("kevin@test.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_shouldReturnTokens_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("kevin@test.com");
        request.setPassword("correctPassword");

        User storedUser = buildStoredUser("kevin@test.com", "encodedPassword");

        when(userRepository.findByEmail("kevin@test.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(storedUser)).thenReturn("jwt-access-token");
        when(refreshTokenService.create(storedUser))
                .thenReturn(new RefreshToken("refresh-token-value", storedUser, Instant.now().plusSeconds(60)));

        LoginResult result = userService.login(request);

        assertThat(result.getAccessToken()).isEqualTo("jwt-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-value");
        assertThat(result.getEmail()).isEqualTo("kevin@test.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }
}
