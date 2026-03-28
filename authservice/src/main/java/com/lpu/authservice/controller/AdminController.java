package com.lpu.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lpu.authservice.service.MentorApplicationService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MentorApplicationService service;

    // APPROVE MENTOR → 200 OK
    @PutMapping("/mentors/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approve(id));
    }
}