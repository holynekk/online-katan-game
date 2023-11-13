package com.group12.service;

import com.group12.entity.PasswordResetToken;
import com.group12.entity.User;
import com.group12.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for password reset functionality This service provides methods for creating a
 * password reset token for a user, sending a password reset email, and validating the password
 * reset token.
 */
@Service
public class PasswordResetService {

  @Autowired private JavaMailSender mailSender;
  @Autowired private PasswordResetTokenRepository tokenRepository;

  /**
   * Constructs a new PasswordResetService with specified mail sender and token repository.
   *
   * @param mailSender the JavaMailSender used for sending emails
   * @param tokenRepository the PasswordResetTokenRepository for password reset tokens, used to find
   *     users by token and delete tokens
   * @throws IllegalArgumentException if mailSender or tokenRepository is null
   */
  @Autowired
  public PasswordResetService(
      JavaMailSender mailSender, PasswordResetTokenRepository tokenRepository) {
    this.mailSender = mailSender;
    this.tokenRepository = tokenRepository;
  }

  /** Default constructor for PasswordResetService. */
  public PasswordResetService() {
    super();
  }

  /**
   * Creates a password reset token for a given user. Saves the token to the token repository.
   *
   * @param user the user requesting a password reset
   * @param token the password reset token to be created
   * @throws IllegalArgumentException if user or token is null
   */
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

  /**
   * Sends a password reset email to the specified email address. Email should be valid and
   * registered to a user.
   *
   * @param email the email address of the user requesting a password reset
   * @param token the password reset token to be sent with email
   */
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

  /**
   * Validates a password reset token. Checks if the token is found in the token repository and if
   * the token is expired.
   *
   * @param token the password reset token to validate
   * @return true if the token is valid, false otherwise
   */
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

  /**
   * Retrieves the user associated with a valid password reset token.
   *
   * @param token the password reset token
   * @return the User associated with the given token, or null if the token is invalid
   */
  public User getUserByValidatedPasswordResetToken(String token) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token);
    return validatePasswordResetToken(token) ? resetToken.getUser() : null;
  }

  /**
   * Deletes a password reset token registered for a user. This function should be called after a
   * user has successfully reset their password.
   *
   * @param token the password reset token to be deleted. Should be valid.
   */
  public void deletePasswordResetToken(String token) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token);
    tokenRepository.delete(resetToken);
  }
}
