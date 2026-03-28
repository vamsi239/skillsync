package com.lpu.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lpu.authservice.entity.MentorApplication;
import com.lpu.authservice.entity.User;
import com.lpu.authservice.repository.MentorApplicationRepository;
import com.lpu.authservice.repository.UserRepository;

@Service
public class MentorApplicationService {

    @Autowired
    private MentorApplicationRepository repo;

    @Autowired
    private UserRepository userRepo;

    public String apply(MentorApplication app) {
        app.setStatus("PENDING");
        repo.save(app);
        return "Application Submitted";
    }

    public String approve(Long appId) {

        MentorApplication app = repo.findById(appId).orElseThrow();

        User user = userRepo.findById(app.getUserId()).orElseThrow();
        
        if (user.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Admin cannot be converted to mentor");
        }

        user.setRole("ROLE_MENTOR");
        user.setMentorApproved(true);

        app.setStatus("APPROVED");

        userRepo.save(user);
        repo.save(app);

        return "Mentor Approved";
    }
}
