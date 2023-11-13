package com.group12.service;

import com.group12.entity.PasswordResetToken;
import com.group12.entity.User;
import com.group12.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
  @Autowired private JavaMailSender mailSender;
  @Autowired private PasswordResetTokenRepository tokenRepository;

  @Autowired
  public PasswordResetService(
      JavaMailSender mailSender, PasswordResetTokenRepository tokenRepository) {
    this.mailSender = mailSender;
    this.tokenRepository = tokenRepository;
  }

  public PasswordResetService() {
    super();
  }

  public void createPasswordResetTokenForUser(User user, String token) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (token == null) {
      throw new IllegalArgumentException("Token cannot be null");
    }
    PasswordResetToken resetToken = new PasswordResetToken(token, user);
    tokenRepository.save(resetToken);
  }

  public void sendPasswordResetMail(String email, String token) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(email);
    mailMessage.setSubject("Password Reset Request");
    mailMessage.setText(
        "You made a password reset request. The link will be valid for 24 hours. \nTo reset your password,"
            + " click the link below:\n\n"
            + "group12-katan-backend.onrender.com"
            + "/api/user/reset-password?token="
            + token);
    mailSender.send(mailMessage);
  }

  public boolean validatePasswordResetToken(String token) {
    final PasswordResetToken resetToken = tokenRepository.findByToken(token);
    return isTokenFound(resetToken) && !isTokenExpired(resetToken);
  }

  private boolean isTokenFound(PasswordResetToken resetToken) {
    return resetToken != null;
  }

  private boolean isTokenExpired(PasswordResetToken resetToken) {
    if (resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
      tokenRepository.delete(resetToken);
      return true;
    }
    return false;
  }

  public User getUserByValidatedPasswordResetToken(String token) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token);
    return validatePasswordResetToken(token) ? resetToken.getUser() : null;
  }

  public void deletePasswordResetToken(String token) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token);
    tokenRepository.delete(resetToken);
  }
}
