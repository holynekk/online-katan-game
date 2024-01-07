package com.group12.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.entity.SessionCookieToken;
import com.group12.entity.User;
import com.group12.service.PasswordResetService;
import com.group12.service.SessionCookieTokenService;
import com.group12.util.EncryptDecryptUtil;
import com.group12.util.HashUtil;
import com.group12.util.SecureStringUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.group12.api.server.UserApi.SECRET_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserRepositoryTests {

  @Autowired private MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean private UserRepository userRepository;

  @MockBean private GameRepository gameRepository;

  @MockBean private GameHistoryRepository gameHistoryRepository;

  @MockBean private SessionCookieTokenService sessionCookieTokenService;

  @MockBean private PasswordResetService passwordResetService;

  @Test
  public void createUserTest() throws Exception {
    User newUser = new User();

    String encryptedUsername = EncryptDecryptUtil.encryptAes("holynekk", SECRET_KEY);
    newUser.setUsername(encryptedUsername);
    String salt = SecureStringUtil.randomString(16);
    newUser.setSalt(salt);
    String passwordHash = HashUtil.bcrypt("holynekk123", salt);
    newUser.setPasswordHash(passwordHash);
    newUser.setDisplayName("HolyNekK");
    newUser.setEmail("holynekk@gmail.com");
    newUser.setLastPasswordUpdate(LocalDateTime.now());

    User savedUser = new User();
    savedUser.setUserId(1);
    savedUser.setUsername(EncryptDecryptUtil.encryptAes("holynekk", SECRET_KEY));
    savedUser.setSalt(SecureStringUtil.randomString(16));
    savedUser.setPasswordHash(HashUtil.bcrypt("holynekk123", salt));
    savedUser.setDisplayName("HolyNekK");
    savedUser.setEmail("holynekk@gmail.com");
    savedUser.setLastPasswordUpdate(LocalDateTime.now());

    Mockito.when(userRepository.save(newUser)).thenReturn(savedUser);

    String url = "/api/user";
    mockMvc
        .perform(
            post(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isOk())
        .andExpect(content().string("1"));
  }
}
