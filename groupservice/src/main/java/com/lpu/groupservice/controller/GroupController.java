package com.lpu.groupservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lpu.groupservice.config.UserContext;
import com.lpu.groupservice.dto.GroupRequestDTO;
import com.lpu.groupservice.dto.GroupResponseDTO;
import com.lpu.groupservice.service.GroupService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService service;
    private final UserContext userContext;

    // CREATE → 201 CREATED
    @PostMapping
    public ResponseEntity<GroupResponseDTO> create(@RequestBody GroupRequestDTO dto,
                                                  HttpServletRequest request) {

        Long userId = userContext.getUserId(request);

        return ResponseEntity.status(201).body(service.create(dto, userId));
    }

    //  GET ALL → 200 OK
    @GetMapping
    public ResponseEntity<List<GroupResponseDTO>> getAllGroups() {
        return ResponseEntity.ok(service.getAllGroups());
    }

    // JOIN → 200 OK
    @PostMapping("/{id}/join")
    public ResponseEntity<String> join(@PathVariable Long id,
                                      HttpServletRequest request) {

        Long userId = userContext.getUserId(request);

        return ResponseEntity.ok(service.requestJoin(id, userId));
    }

    // APPROVE → 200 OK
    @PutMapping("/{id}/approve/{userId}")
    public ResponseEntity<String> approve(@PathVariable Long id,
                                          @PathVariable Long userId,
                                          HttpServletRequest request) {

        Long creatorId = userContext.getUserId(request);

        return ResponseEntity.ok(service.approve(id, creatorId, userId));
    }

    // REMOVE → 200 OK
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<String> remove(@PathVariable Long id,
                                         @PathVariable Long userId,
                                         HttpServletRequest request) {

        Long creatorId = userContext.getUserId(request);

        return ResponseEntity.ok(service.remove(id, creatorId, userId));
    }
}