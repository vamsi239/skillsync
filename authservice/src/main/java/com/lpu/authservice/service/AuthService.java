package com.lpu.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lpu.authservice.entity.User;
import com.lpu.authservice.exception.CustomException;
import com.lpu.authservice.repository.UserRepository;
import com.lpu.authservice.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    // REGISTER
    public String register(User user) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new CustomException("Email already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        userRepo.save(user);

        return "Registered successfully";
    }

    // LOGIN
    public String login(String email, String password) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new CustomException("Invalid password");
        }

        return jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
    
    
    
}