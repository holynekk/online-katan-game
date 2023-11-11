package com.group12.api.server;

import com.group12.api.request.leaderboard.LeaderboardRequest;
import com.group12.api.response.LeaderBoardResponse;
import com.group12.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/leaderboard")
public class LeaderBoardApi {

  @Autowired private ScoreRepository repository;

  @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<LeaderBoardResponse>> getLeaderboard(
      @RequestBody LeaderboardRequest leaderboardRequest) {

    String timeInterval = leaderboardRequest.getTimeInterval();
    List<String> resultSet;
    List<LeaderBoardResponse> response = new ArrayList<>();

    System.out.println(LocalDateTime.now().getMonthValue());
    if (timeInterval.equals("monthly")) {
      resultSet = repository.getLeaderBoardForMonth(LocalDateTime.now().getMonthValue());
    } else if (timeInterval.equals("weekly")) {
      resultSet = repository.getLeaderBoardForWeek(LocalDateTime.now());
    } else {
      resultSet = repository.getLeaderBoardForAll();
    }

    for (String data : resultSet) {
      String first = data.split(",")[0], second = data.split(",")[1];
      response.add(new LeaderBoardResponse(first, Integer.parseInt(second)));
    }

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
