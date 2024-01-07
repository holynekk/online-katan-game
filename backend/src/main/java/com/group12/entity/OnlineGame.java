package com.group12.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * The {@code OnlineGame} class represents the state of an online game session. It holds information
 * about the players, the turn count, the longest road length, and the longest road user.
 */
public class OnlineGame {
  private int turnCount;
  private int longestRoadLength;
  private String longestRoadUser;

  private List<String> playerList;
  private List<String> userColorList;
  private List<String> userReadyList;
  private Stack<String> playerColors;

  public OnlineGame() {
    this.turnCount = 0;
    this.longestRoadLength = -1;
    this.longestRoadUser = "";
    this.playerList = new ArrayList<>();
    this.userColorList = new ArrayList<>();
    this.userReadyList = new ArrayList<>();
    playerColors = new Stack<>();
    playerColors.addAll(Arrays.asList("pink", "purple", "blue", "green", "orange", "red"));
  }

  public int getTurnCount() {
    return turnCount;
  }

  public void setTurnCount(int turnCount) {
    this.turnCount = turnCount;
  }

  public int getLongestRoadLength() {
    return longestRoadLength;
  }

  public void setLongestRoadLength(int longestRoadLength) {
    this.longestRoadLength = longestRoadLength;
  }

  public String getLongestRoadUser() {
    return longestRoadUser;
  }

  public void setLongestRoadUser(String longestRoadUser) {
    this.longestRoadUser = longestRoadUser;
  }

  public List<String> getPlayerList() {
    return playerList;
  }

  public void setPlayerList(List<String> playerList) {
    this.playerList = playerList;
  }

  public List<String> getUserColorList() {
    return userColorList;
  }

  public void setUserColorList(List<String> userColorList) {
    this.userColorList = userColorList;
  }

  public List<String> getUserReadyList() {
    return userReadyList;
  }

  public void setUserReadyList(List<String> userReadyList) {
    this.userReadyList = userReadyList;
  }

  public Stack<String> getPlayerColors() {
    return playerColors;
  }

  public void setPlayerColors(Stack<String> playerColors) {
    this.playerColors = playerColors;
  }

  // -----------------------------------------------------

  public String getNewColor() {
    return this.playerColors.pop();
  }

  public void addPlayer(String username) {
    this.playerList.add(username);
  }

  public void removePlayer(String username) {
    playerList.remove(username);
  }

  public void addUserColor(String color) {
    this.userColorList.add(color);
  }

  public void removeUserColor(String color) {
    this.userColorList.remove(color);
  }

  public void addUserReady(String username) {
    this.userReadyList.add(username);
  }

  public void removeUserReady(String username) {
    this.userReadyList.remove(username);
  }

  public void addPlayerColors(String color) {
    this.playerColors.add(color);
  }

  public String getFirstTurnUsername() {
    return this.playerList.get(0);
  }

  public String getTurnUsername() {
    return this.playerList.get(this.turnCount % this.playerList.size());
  }

  public String getSkipTurnUsername() {
    return this.playerList.get(++this.turnCount % this.playerList.size());
  }

  public String getSetupTurnUsername() {
    return this.playerList.get(
        this.playerList.size() - 1 - (++this.turnCount % this.playerList.size()));
  }

  public void incrementTurnCount() {
    this.turnCount++;
  }
}
