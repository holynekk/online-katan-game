package com.group12.controller;

import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    // Endpoint to request a password reset
    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestReset(@RequestParam("email") String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) { // CHECK
            String token = UUID.randomUUID().toString();
            passwordResetService.createPasswordResetTokenForUser(user, token);
            passwordResetService.sendPasswordResetMail(userEmail, token);
            return ResponseEntity.ok("Password reset link sent to email!");
        } else {
            return ResponseEntity.badRequest().body("User with email address not found.");
        }
    }

    // Endpoint to reset the password
    @PostMapping("/reset-password")
    public ResponseEntity<?> setNewPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword) {
        ;// TOKEN VALIDATION AND SETTING PASSWORD
        return null;
    }
}
