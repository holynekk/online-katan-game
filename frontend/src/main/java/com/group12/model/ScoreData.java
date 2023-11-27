package com.group12.model;

public class ScoreData {
  private String displayName;

  private int totalWins;

  private int totalScore;

  public ScoreData() {}

  public ScoreData(String displayName, int totalWins, int totalScore) {
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
