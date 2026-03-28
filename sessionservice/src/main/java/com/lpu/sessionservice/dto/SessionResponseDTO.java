package com.lpu.sessionservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponseDTO {

    private Long id;
    private Long userId;
    private Long mentorId;
    private String status;
}
