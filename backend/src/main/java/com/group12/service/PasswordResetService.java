package com.group12.service;

import com.group12.entity.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.group12.entity.User;
import com.group12.repository.PasswordResetTokenRepository;
import java.util.Calendar;


@Service
public class PasswordResetService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public void createPasswordResetTokenForUser(User user, String token) {
        if(user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);
    }

    public void sendPasswordResetMail(String email, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("To reset your password, click the link below:\n" + "https://group12-katan-backend.onrender.com" + "/reset-password?token=" + token); // fix the link
        mailSender.send(mailMessage);
    }
    public boolean validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = tokenRepository.findByToken(token);
        return isTokenFound(passToken) && !isTokenExpired(passToken);
    }
    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }
    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
    public User getUserByValidatedPasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        return validatePasswordResetToken(token) ? resetToken.getUser() : null;
    }
}

