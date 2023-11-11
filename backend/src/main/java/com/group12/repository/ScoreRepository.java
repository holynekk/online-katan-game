package com.group12.repository;

import com.group12.entity.Score;
import com.group12.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {

  @Query(
      value =
          "SELECT ku.displayName, SUM(s.totalScore) AS total FROM Score s, User ku WHERE ku.userId = s.user.userId GROUP BY ku.userId ORDER BY total DESC")
  List<String> getLeaderBoardForAll();

  @Query(
      value =
          "SELECT ku.displayName, SUM(s.totalScore) AS total FROM Score s, User ku WHERE ku.userId = s.user.userId and MONTH(s.history) = ?1 GROUP BY ku.userId ORDER BY total DESC")
  List<String> getLeaderBoardForMonth(int month);

  @Query(
      value =
          "SELECT ku.displayName, SUM(s.totalScore) AS total FROM Score s, User ku WHERE ku.userId = s.user.userId and DATEDIFF(?1, s.history) < 7 GROUP BY ku.userId ORDER BY total DESC")
  List<String> getLeaderBoardForWeek(LocalDateTime dateTime);
}
