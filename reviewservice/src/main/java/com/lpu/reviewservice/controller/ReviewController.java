package com.lpu.reviewservice.controller;

import com.lpu.reviewservice.entity.Review;
import com.lpu.reviewservice.service.ReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    // ADD REVIEW → 201 CREATED
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return ResponseEntity.status(201).body(service.addReview(review));
    }

    // GET REVIEWS → 200 OK
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long mentorId) {
        return ResponseEntity.ok(service.getReviews(mentorId));
    }
}