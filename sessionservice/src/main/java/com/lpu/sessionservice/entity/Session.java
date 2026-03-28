package com.lpu.sessionservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessions")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long mentorId;

    private String status; // PENDING, ACCEPTED, REJECTED
}