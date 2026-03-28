package com.lpu.skillservice.controller;

import com.lpu.skillservice.dto.SkillResponseDTO;
import com.lpu.skillservice.service.SkillService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillService service;

    // GET → 200 OK
    @GetMapping
    public ResponseEntity<List<SkillResponseDTO>> getAllSkills() {
        List<SkillResponseDTO> skills = service.getAllSkills();
        return ResponseEntity.ok(skills);
    }
}