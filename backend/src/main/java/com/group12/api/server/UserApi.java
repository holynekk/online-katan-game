package com.group12.api.server;

import com.group12.api.request.auth.UserCreateRequest;
import com.group12.api.request.auth.UserUpdateRequest;
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

/**
 * REST controller for managing user-related operations. This class handles API requests for user
 * creation, retrieval, and finding users by reset tokens.
 */
@RestController
@RequestMapping("/api/user")
public class UserApi {

    /**
     * Secret key to encrypt user related credentials.
     */
    public static final String SECRET_KEY = "TheSecretKey2468";

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Create user endpoint. Email and username should be unique. After validating the information is
     * unique, user credentials (username and password) is hashed and saved into database with all
     * other information.
     *
     * @param userRequest - custom request object to validate form fields in request body.
     * @return - string message according to response status.
     * @throws Exception - encryption related exceptions.
     */
    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createUser(@RequestBody UserCreateRequest userRequest)
            throws Exception {
        String encryptedUsername =
                EncryptDecryptUtil.encryptAes(userRequest.getUsername().toLowerCase(), SECRET_KEY);

        if (repository.findByUsername(encryptedUsername).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User with username " + userRequest.getUsername() + " already exists.");
        }

        User user = new User();

        user.setUsername(encryptedUsername);

        String salt = SecureStringUtil.randomString(16);
        user.setSalt(salt);

        String passwordHash = HashUtil.bcrypt(userRequest.getPassword(), salt);
        user.setPasswordHash(passwordHash);

        user.setDisplayName(userRequest.getDisplayName());
        user.setEmail(userRequest.getEmail());
        user.setLastPasswordUpdate(LocalDateTime.now());

        User saved = repository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("New user has been created : " + user.getDisplayName());
    }

    /**
     * Retrieves a user based on the provided username after encrypting it.
     *
     * @param providedUsername The username of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional otherwise.
     * @throws Exception If encryption of the username fails.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<User> getUser(@RequestParam(name = "username") String providedUsername)
            throws Exception {

        String encryptedUsername =
                EncryptDecryptUtil.encryptAes(providedUsername.toLowerCase(), SECRET_KEY);
        return repository.findByUsername(encryptedUsername);
    }

    @PutMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequest userRequest)
            throws Exception {
        String encryptedUsername =
                EncryptDecryptUtil.encryptAes(userRequest.getUsername().toLowerCase(), SECRET_KEY);

        Optional<User> user = repository.findByUsername(encryptedUsername);
        if (user.isPresent()) {
            User userObj = user.get();

            userObj.setFirstName(userRequest.getFirstName());
            userObj.setLastName(userRequest.getLastName());
            userObj.setDisplayName(userRequest.getDisplayName());
            userObj.setLastPasswordUpdate(LocalDateTime.now());
            repository.save(userObj);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("User with username " + userRequest.getUsername() + " has been updated.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + userRequest.getUsername() + " could not be found.");
        }
    }

    @DeleteMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public void deleteUser() {
    }

    /**
     * Endpoint to find a user by their password reset token.
     *
     * @param token The password reset token.
     * @return A ResponseEntity containing the User associated with the valid token, or an error
     * message if the token is invalid.
     */
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
