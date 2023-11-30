package com.group12.entity;

import javax.persistence.*;

@Entity
@Table(name = "game_info")
public class Game {

  @Id
  @Column(name = "game_id")
  private int gameId;

  private String gameName;

  private String gameDescription;

  private String gamePassword;

  private Boolean passwordRequired;

  @OneToOne()
  @JoinColumn(name = "game_leader", referencedColumnName = "user_id")
  private User gameLeader;

  private Boolean isOnline;

  private Boolean isStarted;

  private Boolean isFinished;

  public Game() {}

  public Game(
      String gameName,
      String gameDescription,
      String gamePassword,
      Boolean passwordRequired,
      User gameLeader,
      Boolean isOnline,
      Boolean isStarted,
      Boolean isFinished) {
    this.gameName = gameName;
    this.gameDescription = gameDescription;
    this.gamePassword = gamePassword;
    this.passwordRequired = passwordRequired;
    this.gameLeader = gameLeader;
    this.isOnline = isOnline;
    this.isStarted = isStarted;
    this.isFinished = isFinished;
  }

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

  public User getGameLeader() {
    return gameLeader;
  }

  public void setGameLeader(User gameLeader) {
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
}
