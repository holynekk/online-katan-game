package com.group12.api.server.auth;

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

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserApi {
  public static final String SECRET_KEY = "TheSecretKey2468";

  @Autowired private UserRepository repository;

  @PostMapping(
      value = "/user",
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
    user.setMaxScore(0);
    user.setLastPasswordUpdate(LocalDateTime.now());

    User saved = repository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body("New user has been created : " + user.getDisplayName());
  }

  @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  public Optional<User> getUser(@RequestParam(name = "username") String providedUsername)
      throws Exception {

    String encryptedUsername =
        EncryptDecryptUtil.encryptAes(providedUsername.toLowerCase(), SECRET_KEY);
    return repository.findByUsername(encryptedUsername);
  }
}
