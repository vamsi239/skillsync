package com.lpu.sessionservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lpu.sessionservice.dto.SessionRequestDTO;
import com.lpu.sessionservice.dto.SessionResponseDTO;
import com.lpu.sessionservice.repository.SessionRepository;
import com.lpu.sessionservice.service.SessionService;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionService service;

    @Autowired
    private SessionRepository repo;

    // BOOK SESSION → 201 CREATED
    @PostMapping
    public ResponseEntity<SessionResponseDTO> book(@RequestBody SessionRequestDTO dto) {
        return ResponseEntity.status(201).body(service.book(dto));
    }

    // UPDATE STATUS → 200 OK
    @PutMapping("/{id}/{status}")
    public ResponseEntity<SessionResponseDTO> update(@PathVariable Long id,
                                                    @PathVariable String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    // CHECK COMPLETED → 200 OK
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkCompleted(
            @RequestParam Long userId,
            @RequestParam Long mentorId) {

        boolean result = repo.existsByUserIdAndMentorIdAndStatus(
                userId, mentorId, "COMPLETED");

        return ResponseEntity.ok(result);
    }

    // COMPLETE SESSION → 200 OK
    @PutMapping("/{id}/complete")
    public ResponseEntity<SessionResponseDTO> completeSession(@PathVariable Long id) {
        return ResponseEntity.ok(service.updateStatus(id, "COMPLETED"));
    }
}