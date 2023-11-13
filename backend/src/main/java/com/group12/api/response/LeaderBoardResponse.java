package com.group12.api.response;

/**
 * Represents a response for leaderboard-related requests. This class holds the display name and
 * score of a user for leaderboard purposes.
 */
public class LeaderBoardResponse {

  private String displayName;

  private int score;

  public LeaderBoardResponse(String displayName, int score) {
    this.displayName = displayName;
    this.score = score;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }
}
