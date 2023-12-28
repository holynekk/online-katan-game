package com.group12.api.request.game;

public class GameCreateRequest {
  private String gameName;

  private String gameDescription;

  private String gamePassword;

  private Boolean passwordRequired;

  private int gameLeader;

  private Boolean isOnline;

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

  public String getGamePassword() {
    return gamePassword;
  }

  public void setGamePassword(String gamePassword) {
    this.gamePassword = gamePassword;
  }

  public Boolean getPasswordRequired() {
    return passwordRequired;
  }

  public void setPasswordRequired(Boolean passwordRequired) {
    this.passwordRequired = passwordRequired;
  }

  public int getGameLeader() {
    return gameLeader;
  }

  public void setGameLeader(int gameLeader) {
    this.gameLeader = gameLeader;
  }

  public Boolean getOnline() {
    return isOnline;
  }

  public void setOnline(Boolean online) {
    isOnline = online;
  }
}
