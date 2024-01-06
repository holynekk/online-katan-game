package com.group12.api.request.game;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/** Custom request type to validate request body object for game history creation. */
public class GameHistoryCreateRequest {

  private int gameId;

  private String username;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime time;

  private int didWon;

  private int totalScore;

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public int getDidWon() {
    return didWon;
  }

  public void setDidWon(int didWon) {
    this.didWon = didWon;
  }

  public int getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(int totalScore) {
    this.totalScore = totalScore;
  }

  @Override
  public String toString() {
    return "GameHistoryCreateRequest{"
        + "gameId="
        + gameId
        + ", username='"
        + username
        + '\''
        + ", time="
        + time
        + ", didWon="
        + didWon
        + ", totalScore="
        + totalScore
        + '}';
  }
}
