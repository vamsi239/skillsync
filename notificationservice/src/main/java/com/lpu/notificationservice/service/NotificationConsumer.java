package com.lpu.notificationservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lpu.notificationservice.config.RabbitConfig;
import com.lpu.notificationservice.dto.NotificationEvent;
import com.lpu.notificationservice.entity.Notification;
import com.lpu.notificationservice.repository.NotificationRepository;

@Service
public class NotificationConsumer {

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationRepository repo;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consume(NotificationEvent event) {

        // Send email
        emailService.sendEmail(event.getEmail(), event.getMessage());

        // Save in DB
        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .message(event.getMessage())
                .type(event.getType())
                .status("SENT")
                .build();

        repo.save(notification);

        System.out.println("📧 Email sent to: " + event.getEmail());
    }
}
