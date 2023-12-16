package com.group12.model;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Random;
import com.group12.helper.GameHelper;

import static com.group12.controller.GameController.*;

public class CPUPlayer {
  private String color;

  public String displayName;

  private ArrayList<String> ownedCircles;
  private ArrayList<String> ownedCities;
  private ArrayList<String> ownedRoads;

  private int hillResource;
  private int mountainResource;
  private int forestResource;
  private int fieldResource;
  private int pastureFieldResource;
  private int longestRoadLength;
  private int score;

  public CPUPlayer(String color, String displayName) {
    this.displayName = displayName;
    this.color = color;
    this.ownedCircles = new ArrayList<>();
    this.ownedCities = new ArrayList<>();
    this.ownedRoads = new ArrayList<>();
    this.hillResource = 10;
    this.mountainResource = 10;
    this.forestResource = 10;
    this.fieldResource = 10;
    this.pastureFieldResource = 10;
    this.score = 0;
  }

  public void buildRoad(AnchorPane anchorPane, String roadId) {
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")
          && !occupiedEdges.contains(node.getId())) {
        if (node.getId().equals(roadId)) {
          node.setStyle(String.format("-fx-fill: %s;", this.color));
          node.setOnMouseClicked(null);
          node.setVisible(true);
          node.setCursor(Cursor.DEFAULT);
        } else {
          node.setVisible(false);
        }
      }
    }
    this.hillResource--;
    this.forestResource--;
    occupiedEdges.add(roadId);
    this.addOwnedRoads(roadId);
    longestRoadLength = GameHelper.findLongestRoadLength(ownedRoads);
    longestOfEveryone = Math.max(longestRoadLength, longestOfEveryone);
  }

  public void buildSettlement(AnchorPane anchorPane, String settlementId) {
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Circle") && !occupiedCircles.contains(node.getId())) {
        if (node.getId().equals(settlementId)) {
          node.setStyle(String.format("-fx-fill: %s;", this.color));
          node.setOnMouseClicked(null);
          node.setVisible(true);
          node.setCursor(Cursor.DEFAULT);
        } else {
          node.setVisible(false);
        }
      }
    }
    this.hillResource--;
    this.forestResource--;
    this.fieldResource--;
    this.pastureFieldResource--;
    occupiedCircles.add(settlementId);
    this.increaseScore(1);
    ownedCircles.add(settlementId);
  }

  public void upgradeToCity(AnchorPane anchorPane) {
    ArrayList<String> optionalUpgrades = new ArrayList<>(this.getOwnedCircles());
    optionalUpgrades.removeAll(this.getOwnedCities());
    Random rnd = new Random();
    if (!optionalUpgrades.isEmpty()) {
      for (Node node : anchorPane.getChildren()) {
        if (node.getClass().getName().contains("Circle")
            && node.getId().equals(optionalUpgrades.get(rnd.nextInt(optionalUpgrades.size())))) {
          node.setScaleX(1.3);
          node.setScaleY(1.3);
          this.ownedCities.add(node.getId());
          this.increaseScore(1);
          break;
        }
      }
    }
  }

  public String buildSettlementAtSetup(
      AnchorPane anchorPane, ArrayList<String> optionalSettlements) {
    Random rnd = new Random();
    int rndInt = rnd.nextInt(optionalSettlements.size());
    String circleId = optionalSettlements.get(rndInt);
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (node.getId().equals(optionalSettlements.get(rndInt))) {
          node.setVisible(true);
          node.setStyle(String.format("-fx-fill: %s;", this.color));
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          addOwnedCircles(circleId);
        }
      }
    }
    return circleId;
  }

  public String buildRoadAtSetup(AnchorPane anchorPane, String circleId) {
    ArrayList<String> optionalRoads = new ArrayList<>();
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")
          && (node.getId().startsWith(circleId + "-") || node.getId().endsWith("-" + circleId))) {
        optionalRoads.add(node.getId());
      }
    }

    Random rnd = new Random();
    int rndInt = rnd.nextInt(optionalRoads.size());
    String roadId = optionalRoads.get(rndInt);
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (node.getId().equals(roadId)) {
          node.setVisible(true);
          node.setStyle(String.format("-fx-fill: %s;", this.color));
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          addOwnedRoads(roadId);
          this.longestRoadLength = GameHelper.findLongestRoadLength(ownedRoads);
          longestOfEveryone = Math.max(longestRoadLength, longestOfEveryone);
        }
      }
    }
    return roadId;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void increaseHillResource(int hillResource) {
    this.hillResource += hillResource;
  }

  public void decreaseHillResource(int hillResource) {
    this.hillResource -= hillResource;
  }

  public void increaseMountainResource(int mountainResource) {
    this.mountainResource += mountainResource;
  }

  public void decreaseMountainResource(int mountainResource) {
    this.mountainResource -= mountainResource;
  }

  public void increaseForestResource(int forestResource) {
    this.forestResource += forestResource;
  }

  public void decreaseForestResource(int forestResource) {
    this.forestResource -= forestResource;
  }

  public void increaseFieldResource(int fieldResource) {
    this.fieldResource += fieldResource;
  }

  public void decreaseFieldResource(int fieldResource) {
    this.fieldResource -= fieldResource;
  }

  public void increasePastureFieldResource(int pastureFieldResource) {
    this.pastureFieldResource += pastureFieldResource;
  }

  public void decreasePastureFieldResource(int pastureFieldResource) {
    this.pastureFieldResource -= pastureFieldResource;
  }

  public void increaseScore(int num) {
    this.score += num;
  }

  public int getScore() {
    return (ifHasLongestRoad() && longestRoadLength >= 5) ? score + 2 : score;
  }

  public int getLongestRoadLength() {
    return longestRoadLength;
  }

  public boolean ifHasLongestRoad() {
    return longestRoadLength == longestOfEveryone;
  }

  public int getHillResource() {
    return hillResource;
  }

  public int getMountainResource() {
    return mountainResource;
  }

  public int getForestResource() {
    return forestResource;
  }

  public int getFieldResource() {
    return fieldResource;
  }

  public int getPastureFieldResource() {
    return pastureFieldResource;
  }

  public int getTotalResources() {
    return hillResource + mountainResource + forestResource + fieldResource + pastureFieldResource;
  }

  public ArrayList<String> getOwnedCircles() {
    return ownedCircles;
  }

  public void addOwnedCircles(String circleId) {
    this.ownedCircles.add(circleId);
    this.score++;
  }

  public ArrayList<String> getOwnedCities() {
    return ownedCities;
  }

  public void setOwnedCities(ArrayList<String> ownedCities) {
    this.ownedCities = ownedCities;
    this.score++;
  }

  public ArrayList<String> getOwnedRoads() {
    return ownedRoads;
  }

  public void addOwnedRoads(String circleId) {
    this.ownedRoads.add(circleId);
  }

  public String getResourceToString() {
    return "Resources{"
        + "hillResource="
        + hillResource
        + ", mountainResource="
        + mountainResource
        + ", forestResource="
        + forestResource
        + ", fieldResource="
        + fieldResource
        + ", pastureFieldResource="
        + pastureFieldResource
        + '}';
  }
}
