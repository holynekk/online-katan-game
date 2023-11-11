package com.group12.api.server;

import com.group12.api.request.auth.UserCreateRequest;
import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.util.EncryptDecryptUtil;
import com.group12.util.HashUtil;
import com.group12.util.SecureStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.group12.service.PasswordResetService;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserApi {
  public static final String SECRET_KEY = "TheSecretKey2468";

  @Autowired private UserRepository repository;

  @PostMapping(
          value = "",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> createUser(@RequestBody UserCreateRequest userRequest)
          throws Exception {

    User user = new User();

    String encryptedUsername =
            EncryptDecryptUtil.encryptAes(userRequest.getUsername().toLowerCase(), SECRET_KEY);
    user.setUsername(encryptedUsername);

    String salt = SecureStringUtil.randomString(16);
    user.setSalt(salt);

    String passwordHash = HashUtil.bcrypt(userRequest.getPassword(), salt);
    user.setPasswordHash(passwordHash);

    user.setDisplayName(userRequest.getDisplayName());
    user.setEmail(userRequest.getEmail());
    user.setMaxScore(0);
    user.setLastPasswordUpdate(LocalDateTime.now());

    User saved = repository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED)
            .body("New user has been created : " + user.getDisplayName());
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public Optional<User> getUser(@RequestParam(name = "username") String providedUsername)
          throws Exception {
    String encryptedUsername =
            EncryptDecryptUtil.encryptAes(providedUsername.toLowerCase(), SECRET_KEY);
    return repository.findByUsername(encryptedUsername);
  }

  @Autowired
  private PasswordResetService passwordResetService;

  @GetMapping("/find-user-by-reset-token")
  public ResponseEntity<?> findUserByResetToken(@RequestParam String token) {
    User user = passwordResetService.getUserByValidatedPasswordResetToken(token);
    if (user != null) {
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found or invalid");
    }
  }
}
