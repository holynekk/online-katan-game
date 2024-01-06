package com.group12.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GameHistoryModel {
  private final SimpleIntegerProperty gameId;

  private final SimpleStringProperty history;

  private final SimpleStringProperty didWon;

  private final SimpleIntegerProperty score;

  public GameHistoryModel(int gameId, String history, String didWon, int score) {
    this.gameId = new SimpleIntegerProperty(gameId);
    this.history = new SimpleStringProperty(history);
    this.didWon = new SimpleStringProperty(didWon);
    this.score = new SimpleIntegerProperty(score);
  }

  public int getGameId() {
    return gameId.get();
  }

  public void setGameId(int gameId) {
    this.gameId.set(gameId);
  }

  public String getHistory() {
    return history.get();
  }

  public void setHistory(String history) {
    this.history.set(history);
  }

  public String isDidWon() {
    return didWon.get();
  }

  public void setDidWon(String didWon) {
    this.didWon.set(didWon);
  }

  public int getScore() {
    return score.get();
  }

  public void setScore(int score) {
    this.score.set(score);
  }
}
