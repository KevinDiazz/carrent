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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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

    @Test
    void shouldRegisterUserWhenEmailDoesNotExist() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Kevin");
        request.setEmail("test@test.com");
        request.setPassword("12345678");

        User user = new User();

        RegisterResponse response = new RegisterResponse(
                "Kevin",
                "test@test.com",
                Role.USER
        );

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("12345678"))
                .thenReturn("hashedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        // Act
        RegisterResponse result = userService.registerUser(request);

        // Assert
        assertEquals(response, result);
        verify(userRepository).findByEmail("test@test.com");
        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Kevin");
        request.setEmail("test@test.com");
        request.setPassword("12345678");

        User existingUser = new User();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(existingUser));

        // Act + Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );
        verify(userMapper, never()).toEntity(request);

        verify(passwordEncoder, never()).encode("12345678");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("12345678");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);
        user.setName("Kevin");

        RefreshToken refreshToken = new RefreshToken(
                "refresh-token",
                user,
                Instant.now().plusSeconds(3600)
        );

        LoginResult expected = new LoginResult(
                "access-token",
                "refresh-token",
                "test@test.com",
                Role.USER,
                "Kevin"
        );

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("12345678", "hashedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("access-token");

        when(refreshTokenService.create(user))
                .thenReturn(refreshToken);

        // Act
        LoginResult result = userService.login(request);

        // Assert
        assertEquals(expected.getAccessToken(), result.getAccessToken());
        assertEquals(expected.getRefreshToken(), result.getRefreshToken());
        assertEquals(expected.getEmail(), result.getEmail());
        assertEquals(expected.getRole(), result.getRole());
        assertEquals(expected.getName(), result.getName());
        verify(userRepository).findByEmail("test@test.com");

        verify(passwordEncoder)
                .matches("12345678", "hashedPassword");

        verify(jwtService)
                .generateToken(user);

        verify(refreshTokenService)
                .create(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@test.com");
        request.setPassword("12345678");

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        // Verify
        verify(userRepository).findByEmail("unknown@test.com");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);
        user.setName("Kevin");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "hashedPassword"
        )).thenReturn(false);

        // Act + Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );
        verify(userRepository).findByEmail("test@test.com");

        verify(passwordEncoder)
                .matches("wrongPassword", "hashedPassword");

        verifyNoInteractions(jwtService);

        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void shouldGenerateTokensWhenLoginIsSuccessful() {

        LoginRequest request = new LoginRequest();
        request.setEmail("kevin@test.com");
        request.setPassword("password");

        User user = mock(User.class);
        when(user.getPassword()).thenReturn("encoded-password");

        RefreshToken refreshToken = mock(RefreshToken.class);

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encoded-password"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("access-token");

        when(refreshTokenService.create(user))
                .thenReturn(refreshToken);

        LoginResult result = userService.login(request);

        assertNotNull(result);

        verify(jwtService).generateToken(user);
        verify(refreshTokenService).create(user);
    }

    @Test
    void shouldNotGenerateTokensWhenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("kevin@test.com");
        request.setPassword("wrong-password");

        User user = mock(User.class);

        when(user.getPassword()).thenReturn("encoded-password");

        when(userRepository.findByEmail("kevin@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(jwtService, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).create(any(User.class));
    }
}
