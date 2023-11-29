package com.group12.api.response;

/**
 * Represents a response for leaderboard-related requests. This class holds the display name and
 * score of a user for leaderboard purposes.
 */
public class LeaderBoardResponse {

  private String displayName;

  private int totalWins;

  private int totalScore;

  public LeaderBoardResponse(String displayName, int totalWins, int totalScore) {
    this.displayName = displayName;
    this.totalWins = totalWins;
    this.totalScore = totalScore;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int getTotalWins() {
    return totalWins;
  }

  public void setTotalWins(int totalWins) {
    this.totalWins = totalWins;
  }

  public int getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(int totalScore) {
    this.totalScore = totalScore;
  }
}
