package com.lpu.skillservice.controller;

import com.lpu.skillservice.dto.SkillRequestDTO;
import com.lpu.skillservice.dto.SkillResponseDTO;
import com.lpu.skillservice.service.SkillService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/skills")
public class AdminSkillController {

    @Autowired
    private SkillService service;

    // CREATE → 201 CREATED
    @PostMapping
    public ResponseEntity<SkillResponseDTO> addSkill(@RequestBody SkillRequestDTO dto) {
        SkillResponseDTO response = service.addSkill(dto);
        return ResponseEntity.status(201).body(response);
    }

    // DELETE → 200 OK
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        String msg = service.deleteSkill(id);
        return ResponseEntity.ok(msg);
    }
}