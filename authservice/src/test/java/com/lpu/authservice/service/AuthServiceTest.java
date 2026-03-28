package com.lpu.authservice.service;

import com.lpu.authservice.entity.User;
import com.lpu.authservice.exception.CustomException;
import com.lpu.authservice.repository.UserRepository;
import com.lpu.authservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    // ✅ Register: success
    @Test
    void testRegister_Success() {
        User user = new User(null, "test@gmail.com", "pass123", "ROLE_USER", false);
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(encoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenReturn(user);

        String result = authService.register(user);

        assertEquals("Registered successfully", result);
        verify(userRepo, times(1)).save(any(User.class));
    }

    // ❌ Register: email already exists
    @Test
    void testRegister_EmailAlreadyExists() {
        User user = new User(null, "test@gmail.com", "pass123", "ROLE_USER", false);
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        assertThrows(CustomException.class, () -> authService.register(user));
    }

    // ✅ Login: success
    @Test
    void testLogin_Success() {
        User user = new User(1L, "test@gmail.com", "encodedPass", "ROLE_USER", false);
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(encoder.matches("pass123", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "test@gmail.com", "ROLE_USER")).thenReturn("jwt.token.here");

        String token = authService.login("test@gmail.com", "pass123");

        assertEquals("jwt.token.here", token);
    }

    // ❌ Login: wrong password
    @Test
    void testLogin_WrongPassword() {
        User user = new User(1L, "test@gmail.com", "encodedPass", "ROLE_USER", false);
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThrows(CustomException.class, () -> authService.login("test@gmail.com", "wrongPass"));
    }

    // ❌ Login: user not found
    @Test
    void testLogin_UserNotFound() {
        when(userRepo.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> authService.login("unknown@gmail.com", "pass123"));
    }
}
