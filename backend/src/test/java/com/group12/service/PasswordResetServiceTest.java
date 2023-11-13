package com.group12.service;

import com.group12.entity.PasswordResetToken;
import com.group12.entity.User;
import com.group12.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetServiceTest {
  @Mock private JavaMailSender mockMailSender = mock(JavaMailSender.class);

  @Mock
  private PasswordResetTokenRepository mockTokenRepository =
      mock(PasswordResetTokenRepository.class);

  @Mock private User mockUser = mock(User.class);

  @Mock private PasswordResetToken mockResetToken = mock(PasswordResetToken.class);
  @InjectMocks private PasswordResetService passwordResetService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreatePasswordResetTokenForUser_UserIsNull() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordResetService.createPasswordResetTokenForUser(null, "token123");
            });
    assertEquals("User cannot be null", thrown.getMessage());
  }

  @Test
  void testCreatePasswordResetTokenForUser_TokenIsNull() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordResetService.createPasswordResetTokenForUser(mockUser, null);
            });
    assertEquals("Token cannot be null", thrown.getMessage());
  }

  @Test
  public void testCreatePasswordResetTokenForUser() {
    User mockUser = mock(User.class);
    String token = "test-token";

    PasswordResetService service = new PasswordResetService(mockMailSender, mockTokenRepository);

    service.createPasswordResetTokenForUser(mockUser, token);

    verify(mockTokenRepository).save(any(PasswordResetToken.class));
  }

  @Test
  public void testSendPasswordResetMail() {
    String email = "test@example.com";
    String token = "test-token";

    PasswordResetService service = new PasswordResetService(mockMailSender, mockTokenRepository);

    service.sendPasswordResetMail(email, token);

    verify(mockMailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void testValidatePasswordResetToken() {
    String token = "test-token";

    when(mockResetToken.getExpiryDate()).thenReturn(LocalDateTime.now().plusDays(1));

    PasswordResetService service = new PasswordResetService(mockMailSender, mockTokenRepository);

    when(mockTokenRepository.findByToken(token)).thenReturn(mockResetToken);

    boolean isValid = service.validatePasswordResetToken(token);

    assertTrue(isValid);
  }

  @Test
  public void testGetUserByValidatedPasswordResetToken() {
    String token = "test-token";
    PasswordResetService service = new PasswordResetService(mockMailSender, mockTokenRepository);

    when(mockTokenRepository.findByToken(token)).thenReturn(mockResetToken);
    when(mockResetToken.getExpiryDate()).thenReturn(LocalDateTime.now().plusDays(1));
    when(mockResetToken.getUser()).thenReturn(mockUser);

    User result = service.getUserByValidatedPasswordResetToken(token);

    assertEquals(mockUser, result);
  }
}
