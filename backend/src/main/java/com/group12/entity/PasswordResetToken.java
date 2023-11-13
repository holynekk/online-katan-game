package com.group12.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entity class representing a password reset token. This entity stores the token used for password
 * reset, its expiry information, and the associated user.
 */
@Getter
@Setter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
  /** The expiration time for the token, in minutes (default is 1 hour). */
  private static final int EXPIRATION = 60; // 1 hour (60 minutes) expiration

  /** The unique ID of the password reset token. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The password reset token string. */
  private String token;

  /** The password reset token string. */
  @Column(name = "expiry_date")
  private LocalDateTime expiryDate;

  /**
   * The user associated with the password reset token. This is a one-to-one relationship. Appears
   * as a foreign key user_id in the password_reset_token table.
   */
  @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * Constructs a new PasswordResetToken with a specified token and user. The expiry date is
   * calculated, not taken as a parameter.
   *
   * @param token the password reset token string
   * @param user the user who requested the password reset
   */
  public PasswordResetToken(String token, User user) {
    this.token = token;
    this.user = user;
    this.expiryDate = calculateExpiryDate();
  }

  /** Constructs a new PasswordResetToken with no parameters. */
  public PasswordResetToken() {}

  /**
   * Calculates the expiry date of the token based on the current time.
   * Adds EXPIRATION minutes to the current time.
   * @return the calculated expiry date and time
   */
  private LocalDateTime calculateExpiryDate() {
    return LocalDateTime.now().plusMinutes(EXPIRATION);
  }
}
