package com.lpu.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lpu.authservice.entity.MentorApplication;
import com.lpu.authservice.service.MentorApplicationService;

@RestController
@RequestMapping("/user")
public class MentorController {

    @Autowired
    private MentorApplicationService service;

    // APPLY MENTOR → 201 CREATED
    @PostMapping("/mentor-application")
    public ResponseEntity<String> apply(@RequestBody MentorApplication app) {
        return ResponseEntity.status(201).body(service.apply(app));
    }
}