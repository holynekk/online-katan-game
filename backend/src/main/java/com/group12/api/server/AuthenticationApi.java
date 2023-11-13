package com.group12.api.server;

import com.group12.constant.SessionCookieConstant;
import com.group12.entity.SessionCookieToken;
import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.service.SessionCookieTokenService;
import com.group12.service.PasswordResetService;
import com.group12.util.HashUtil;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationApi {

  @Autowired private SessionCookieTokenService tokenService;
  @Autowired private UserRepository userRepository;

  @Autowired private PasswordResetService passwordResetService;

  @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
  public String login(HttpServletRequest request) {
    String encryptedUsername =
        (String) request.getAttribute(SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME);
    SessionCookieToken token = new SessionCookieToken();
    token.setUsername(encryptedUsername);
    String tokenId = tokenService.store(request, token);

    LocalDateTime lastPasswordChange =
        userRepository.findByUsername(encryptedUsername).get().getLastPasswordUpdate();
    if (lastPasswordChange.plusDays(90).isBefore(LocalDateTime.now())) {
      return "Logged in with tokenId: "
          + tokenId
          + ".\nYou have not changed your password for more than 90 days. "
          + "Please change your password.";
    }

    return "Logged in with tokenId: " + tokenId;
  }

  @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
  public String test(HttpServletRequest request) {
    String encryptedUsername =
        (String) request.getAttribute(SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME);

    return "Token is valid. Endpoint accessed at " + LocalDateTime.now();
  }

  @DeleteMapping(value = "/logout", produces = MediaType.TEXT_PLAIN_VALUE)
  public String logout(HttpServletRequest request) {
    tokenService.delete(request);

    return "Logged out!";
  }

  @PostMapping("/password-reset-request")
  public ResponseEntity<?> requestPasswordReset(@RequestParam("email") String userEmail) {

    Optional<User> user = userRepository.findByEmail(userEmail);

    if (user.isPresent()) {
      String token = UUID.randomUUID().toString(); // randomly generated token
      passwordResetService.createPasswordResetTokenForUser(user.get(), token);
      passwordResetService.sendPasswordResetMail(userEmail, token);
      return ResponseEntity.ok("Password reset link is sent to email!");
    } else {
      return ResponseEntity.badRequest()
          .body("There is no user registered with this email address.");
    }
  }

  // Endpoint to reset the password
  @GetMapping("/set-new-password")
  public ResponseEntity<?> setNewPassword(
      @RequestParam("token") String token, @RequestParam("password") String newPassword) {
    User user = passwordResetService.getUserByValidatedPasswordResetToken(token); //
    if (user != null) {
      String newPasswordHash = HashUtil.bcrypt(newPassword, user.getSalt());
      user.setPasswordHash(newPasswordHash);
      user.setLastPasswordUpdate(java.time.LocalDateTime.now());
      userRepository.save(user);
      passwordResetService.deletePasswordResetToken(token);
      return ResponseEntity.ok("Password reset successfully!");
    } else {
      return ResponseEntity.badRequest().body("Your token is invalid.");
    }
  }
}
