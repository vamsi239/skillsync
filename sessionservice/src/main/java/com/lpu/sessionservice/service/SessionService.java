package com.lpu.sessionservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lpu.sessionservice.client.AuthClient;
import com.lpu.sessionservice.dto.NotificationEvent;
import com.lpu.sessionservice.dto.SessionRequestDTO;
import com.lpu.sessionservice.dto.SessionResponseDTO;
import com.lpu.sessionservice.dto.UserDTO;
import com.lpu.sessionservice.entity.Session;
import com.lpu.sessionservice.repository.SessionRepository;
import com.lpu.sessionservice.config.RabbitConfig;
import com.lpu.sessionservice.exception.CustomException;

@Service
public class SessionService {

    @Autowired
    private SessionRepository repo;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // BOOK SESSION
    public SessionResponseDTO book(SessionRequestDTO dto) {

        if (dto.getUserId() == null || dto.getMentorId() == null) {
            throw new CustomException("UserId and MentorId are required");
        }

        Session session = Session.builder()
                .userId(dto.getUserId())
                .mentorId(dto.getMentorId())
                .status("PENDING")
                .build();

        return map(repo.save(session));
    }

    // UPDATE STATUS
    public SessionResponseDTO updateStatus(Long id, String status) {

        Session session = repo.findById(id)
                .orElseThrow(() -> new CustomException("Session not found"));

        session.setStatus(status);
        Session saved = repo.save(session);

        // CALL AUTH SERVICE SAFELY
        UserDTO user;
        try {
            user = authClient.getUserById(session.getUserId());
        } catch (Exception e) {
            throw new CustomException("Auth service unavailable");
        }

        // SEND NOTIFICATION (DON’T BREAK FLOW)
        try {

            if (status.equals("ACCEPTED")) {

                NotificationEvent event = NotificationEvent.builder()
                        .userId(session.getUserId())
                        .email(user.getEmail())
                        .message("Your session has been accepted!")
                        .type("SESSION_ACCEPTED")
                        .build();

                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.ROUTING_KEY,
                        event
                );
            }

            if (status.equals("COMPLETED")) {

                NotificationEvent event = NotificationEvent.builder()
                        .userId(session.getUserId())
                        .email(user.getEmail())
                        .message("Your session is completed. Please give rating and review.")
                        .type("SESSION_COMPLETED")
                        .build();

                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.ROUTING_KEY,
                        event
                );
            }

        } catch (Exception e) {
            
            System.out.println("Notification failed: " + e.getMessage());
        }

        return map(saved);
    }

    private SessionResponseDTO map(Session s) {
        return SessionResponseDTO.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .mentorId(s.getMentorId())
                .status(s.getStatus())
                .build();
    }
}