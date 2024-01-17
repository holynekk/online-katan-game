package com.group12.api.server;

import com.group12.constant.SessionCookieConstant;
import com.group12.entity.SessionCookieToken;
import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.service.SessionCookieTokenService;
import com.group12.util.HashUtil;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationApi {

  @Autowired private SessionCookieTokenService tokenService;
  @Autowired private UserRepository userRepository;

  /**
   * Allows client to log in to the system and creates a session for the current user.
   *
   * @param request - http request sent by client
   * @return - a string response which includes session token id to use in other api endpoints.
   */
  @GetMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> login(HttpServletRequest request) {
    String encryptedUsername =
        (String) request.getAttribute(SessionCookieConstant.REQUEST_ATTRIBUTE_USERNAME);
    Optional<User> optionalUser = userRepository.findByUsername(encryptedUsername);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      SessionCookieToken token = new SessionCookieToken();
      token.setUsername(encryptedUsername);
      String tokenId = tokenService.store(request, token);
      return ResponseEntity.ok()
          .header("username", encryptedUsername)
          .header("userId", Integer.toString(user.getUserId()))
          .body(tokenId);
    } else {
      return ResponseEntity.badRequest().body("Username or Password is incorrect!");
    }
  }

  /**
   * Allows client to log out from the system and deletes the session and session cookie.
   *
   * @param request - http request sent by client
   * @return - string type of message
   */
  @DeleteMapping(value = "/logout", produces = MediaType.TEXT_PLAIN_VALUE)
  public String logout(HttpServletRequest request) {
    tokenService.delete(request);

    return "Logged out!";
  }
}
