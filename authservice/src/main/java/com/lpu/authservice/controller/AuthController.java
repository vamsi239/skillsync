package com.lpu.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lpu.authservice.dto.UserDTO;
import com.lpu.authservice.entity.User;
import com.lpu.authservice.repository.UserRepository;
import com.lpu.authservice.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import com.lpu.authservice.exception.CustomException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepo;

    // REGISTER → 201 CREATED
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return ResponseEntity.status(201).body(authService.register(user));
    }

    // LOGIN → 200 OK
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        String token = authService.login(user.getEmail(), user.getPassword());
        return ResponseEntity.ok(token);
    }

    // GET USER → 200 OK
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));

        return ResponseEntity.ok(new UserDTO(user.getId(), user.getEmail()));
    }
    
    
    
   
}