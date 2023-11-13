package com.group12.repository;

import com.group12.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link PasswordResetToken} entities. Extends {@link JpaRepository} to
 * provide CRUD operations on PasswordResetToken entities. This interface is used handle to database
 * operations for PasswordResetToken entities.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  /**
   * Retrieves a {@link PasswordResetToken} entity using a token string.
   *
   * @param token The token string used to find the PasswordResetToken entity.
   * @return The PasswordResetToken entity if found, or {@code null} if no entity matches the given
   *     token.
   */
  PasswordResetToken findByToken(String token);
}
