package com.group12.api.response;

/** Custom response type to validate request body object for game history response. */
public class GameHistoryResponse {

  private int gameId;

  private String history;

  private boolean didWon;

  private int score;

  public GameHistoryResponse(int gameId, String history, boolean didWon, int score) {
    this.gameId = gameId;
    this.history = history;
    this.didWon = didWon;
    this.score = score;
  }

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
