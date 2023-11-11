package com.group12.api.server;

import com.group12.entity.PasswordResetToken;
import com.group12.entity.User;
import com.group12.repository.PasswordResetTokenRepository;
import com.group12.repository.UserRepository;
import com.group12.service.PasswordResetService;
import com.group12.util.HashUtil;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class PasswordResetController {
    @Autowired
    PasswordResetService passwordResetService;
    @Autowired
    UserRepository userRepository;

    // Endpoint to request a password reset
    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestReset(@RequestParam("email") String userEmail) {

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            passwordResetService.createPasswordResetTokenForUser(user.get(), token);
            passwordResetService.sendPasswordResetMail(userEmail, token);
            return ResponseEntity.ok("Password reset link sent to email!");
        } else {
            return ResponseEntity.badRequest().body("There is no user registered with this email address.");
        }
    }

    // Endpoint to reset the password
    @GetMapping("/reset-password")
    public ResponseEntity<?> setNewPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword) {
        User user = passwordResetService.getUserByValidatedPasswordResetToken(token); //
        if (user != null) {
            String newPasswordHash = HashUtil.bcrypt(newPassword, user.getSalt());
            user.setPasswordHash(newPasswordHash);
            user.setLastPasswordUpdate(java.time.LocalDateTime.now());
            userRepository.save(user);
            passwordResetService.deletePasswordResetToken(token);
            return ResponseEntity.ok("Password reset successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid token.");
        }
    }
}
