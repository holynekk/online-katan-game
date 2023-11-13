package com.group12.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.api.server.LeaderBoardApi;
import com.group12.entity.Score;
import com.group12.entity.User;
import com.group12.repository.ScoreRepository;
import com.group12.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(LeaderBoardApi.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.group12.api.server.*"})
public class LeaderboardApiTest {

  @Autowired MockMvc mockMvc;

  @MockBean LeaderBoardApi leaderBoardApi;

  @MockBean UserRepository userRepository;

  @MockBean ScoreRepository scoreRepository;

  @Before
  public void setUp() {

    User newUser = new User();

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

    Score score = new Score();


  }

  @Test
  @Order(1)
  public void getLeaderBoardForAll() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> body = new HashMap<>();
    body.put("timeInterval", "all");
    body.put("offset", 0);
    body.put("pagination", 10);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/leaderboard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andDo(print())
            .andReturn();

    System.out.println(result.getResponse().getContentAsString());
  }
}
