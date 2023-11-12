package com.group12.api;

import com.group12.api.filter.SessionCookieAuthFilter;
import com.group12.api.filter.SessionCookieTokenFilter;
import com.group12.api.filter.config.SessionCookieFilterConfig;
import com.group12.api.server.AuthenticationApi;
import com.group12.entity.User;
import com.group12.repository.UserRepository;
import com.group12.service.SessionCookieTokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AuthenticationApi.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.group12.api.server.*", "com.group12.service.*"})
public class AuthenticationApiTest {

  @Autowired MockMvc mockMvc;

  @MockBean UserRepository userRepository;

  @MockBean SessionCookieTokenService tokenService;

  User newUser;

  @Before
  public void setUp() {

    newUser = new User();

    newUser.setUsername("278EE5F5F523A7CAF68CD1A222C667DC");
    newUser.setEmail("test_admin@gmail.com");
    newUser.setSalt("wnNJiOOYM3L9OdFq");
    newUser.setPasswordHash("$2y$05$RjjMZBLsL0e1bFTVMVG3SOp4U/mf7qRFaq5GqcwzHVDkuk4NwPJyG");
    newUser.setUserId(1);
    newUser.setFirstName("Test");
    newUser.setLastName("ADMIN");
    newUser.setDisplayName("Admin");
    newUser.setLastPasswordUpdate(LocalDateTime.now());

    userRepository.save(newUser);
  }

  @Test
  @Order(1)
  public void login() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        "Basic "
                            + Base64Utils.encodeToString(
                                "test_admin:adminKatan12group".getBytes())))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andDo(print())
            .andReturn();

    System.out.println(result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  public void logout() throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/api/auth/logout"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andDo(print())
            .andReturn();

    System.out.println(result.getResponse().getContentAsString());
  }
}
