package com.lpu.notificationservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.notificationservice.entity.Notification;
import com.lpu.notificationservice.repository.NotificationRepository;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository repo;

    @GetMapping("/{userId}")
    public List<Notification> get(@PathVariable Long userId) {
        return repo.findByUserId(userId);
    }
}
