package com.group12.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ScoreModel {
  private final SimpleIntegerProperty gameId;

  private final SimpleStringProperty displayName;

  private final SimpleIntegerProperty totalWins;

  private final SimpleIntegerProperty totalScore;

  public ScoreModel(int gameId, String displayName, int totalWins, int score) {
    this.gameId = new SimpleIntegerProperty(gameId);
    this.displayName = new SimpleStringProperty(displayName);
    this.totalWins = new SimpleIntegerProperty(totalWins);
    this.totalScore = new SimpleIntegerProperty(score);
  }

  public int getGameId() {
    return gameId.get();
  }

  public void setGameId(int gameId) {
    this.gameId.set(gameId);
  }

  public String getDisplayName() {
    return displayName.get();
  }

  public void setDisplayName(String dName) {
    displayName.set(dName);
  }

  public int getTotalWins() {
    return totalWins.get();
  }

  public void setTotalWins(int ttlWins) {
    totalWins.set(ttlWins);
  }

  public int getTotalScore() {
    return totalScore.get();
  }

  public void setTotalScore(int ttlScore) {
    totalScore.set(ttlScore);
  }
}
