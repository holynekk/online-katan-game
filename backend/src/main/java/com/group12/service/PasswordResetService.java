package com.group12.service;

import com.group12.entity.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.repository.PasswordResetTokenRepository;

import java.util.Optional;


@Service
public class PasswordResetService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public void createPasswordResetTokenForUser(Optional<User> user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);
    }

    public void sendPasswordResetMail(String email, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("To reset your password, click the link below:\n" + "/reset-password?token=" + token); // fix the link
        mailSender.send(mailMessage);
    }
}

