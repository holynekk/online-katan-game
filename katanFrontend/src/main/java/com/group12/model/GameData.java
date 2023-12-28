package com.group12.model;

public class GameData {
  private int gameId;

  private String gameName;

  private String gameDescription;

  private Boolean passwordRequired;

  private String gameLeader;

  private Boolean isOnline;

  private Boolean isStarted;

  private Boolean isFinished;

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public String getGameDescription() {
    return gameDescription;
  }

  public void setGameDescription(String gameDescription) {
    this.gameDescription = gameDescription;
  }

  public Boolean getPasswordRequired() {
    return passwordRequired;
  }

  public void setPasswordRequired(Boolean passwordRequired) {
    this.passwordRequired = passwordRequired;
  }

  public String getGameLeader() {
    return gameLeader;
  }

  public void setGameLeader(String gameLeader) {
    this.gameLeader = gameLeader;
  }

  public Boolean getOnline() {
    return isOnline;
  }

  public void setOnline(Boolean online) {
    isOnline = online;
  }

  public Boolean getStarted() {
    return isStarted;
  }

  public void setStarted(Boolean started) {
    isStarted = started;
  }

  public Boolean getFinished() {
    return isFinished;
  }

  public void setFinished(Boolean finished) {
    isFinished = finished;
  }

  @Override
  public String toString() {
    return gameName + "(" + gameId + ")";
  }
}
