package com.lpu.sessionservice.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionRequestDTO {

    private Long userId;
    private Long mentorId;
}