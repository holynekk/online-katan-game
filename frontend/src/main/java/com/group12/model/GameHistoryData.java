package com.group12.model;

import java.time.LocalDateTime;

public class GameHistoryData {
  private int gameId;

  private String history;

  private boolean didWon;

  private int score;

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public String getHistory() {
    return history;
  }

  public void setHistory(String history) {
    this.history = history;
  }

  public boolean isDidWon() {
    return didWon;
  }

  public void setDidWon(boolean didWon) {
    this.didWon = didWon;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }
}
