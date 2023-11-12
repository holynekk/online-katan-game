package com.group12.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
  private static final int EXPIRATION = 60 * 24; // 24 hours expiration

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;

  @Column(name = "expiry_date")
  private LocalDateTime expiryDate;

  @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  public PasswordResetToken(String token, User user) {
    this.token = token;
    this.user = user;
    this.expiryDate = calculateExpiryDate();
  }

  public PasswordResetToken() {}

  private LocalDateTime calculateExpiryDate() {
    return LocalDateTime.now().plusMinutes(EXPIRATION);
  }
}
