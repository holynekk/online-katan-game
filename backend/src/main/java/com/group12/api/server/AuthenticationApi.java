package com.group12.api.server;

import com.group12.constant.SessionCookieConstant;
import com.group12.entity.SessionCookieToken;
import com.group12.service.SessionCookieTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationApi {

  @Autowired private SessionCookieTokenService tokenService;

  @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
  public String login(HttpServletRequest request) {
    String encryptedUsername =
            (String) request.getAttribute(SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME);
    SessionCookieToken token = new SessionCookieToken();
    token.setUsername(encryptedUsername);

    String tokenId = tokenService.store(request, token);

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