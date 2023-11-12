package com.group12.api.server;

import com.group12.constant.SessionCookieConstant;
import com.group12.entity.SessionCookieToken;
import com.group12.repository.UserRepository;
import com.group12.service.SessionCookieTokenService;
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

  @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
  public String login(HttpServletRequest request) {
    String encryptedUsername =
            (String) request.getAttribute(SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME);
    SessionCookieToken token = new SessionCookieToken();
    token.setUsername(encryptedUsername);
    String tokenId = tokenService.store(request, token);

    LocalDateTime lastPasswordChange = userRepository.findByUsername(encryptedUsername).get().getLastPasswordUpdate();
    if (lastPasswordChange.plusDays(90).isBefore(LocalDateTime.now())) {
      return "Logged in with tokenId: " + tokenId + ". You have not changed your password for 90 days. " +
              "Please change your password.";
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
}