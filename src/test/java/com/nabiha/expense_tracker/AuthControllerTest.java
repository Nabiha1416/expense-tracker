package com.nabiha.expense_tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthController controller;

    @BeforeEach
    void setUp() {
        JwtService jwtService = new JwtService("this-is-a-test-secret-key-with-at-least-32-characters");
        controller = new AuthController(userRepository, passwordEncoder, jwtService);
    }
    @Test
    void signupSavesUserWithHashedPassword() {
        AppUser user = new AppUser();
        user.setUsername("nabiha");
        user.setPassword("test1234");
        when(userRepository.existsByUsername("nabiha")).thenReturn(false);

        controller.signup(user);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());
        String stored = captor.getValue().getPassword();
        assertNotEquals("test1234", stored);
        assertTrue(passwordEncoder.matches("test1234", stored));
    }
    @Test
    void signupWithExistingUsernameIsRejected() {
        AppUser user = new AppUser();
        user.setUsername("nabiha");
        user.setPassword("test1234");
        when(userRepository.existsByUsername("nabiha")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.signup(user));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }
    @Test
    void loginWithCorrectPasswordReturnsToken() {
        AppUser stored = new AppUser();
        stored.setUsername("nabiha");
        stored.setPassword(passwordEncoder.encode("test1234"));
        when(userRepository.findByUsername("nabiha")).thenReturn(Optional.of(stored));

        AppUser request = new AppUser();
        request.setUsername("nabiha");
        request.setPassword("test1234");

        Map<String, String> result = controller.login(request);

        assertNotNull(result.get("token"));
    }
    @Test
    void loginWithWrongPasswordIsRejected() {
        AppUser stored = new AppUser();
        stored.setUsername("nabiha");
        stored.setPassword(passwordEncoder.encode("test1234"));
        when(userRepository.findByUsername("nabiha")).thenReturn(Optional.of(stored));

        AppUser request = new AppUser();
        request.setUsername("nabiha");
        request.setPassword("wrong123");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}