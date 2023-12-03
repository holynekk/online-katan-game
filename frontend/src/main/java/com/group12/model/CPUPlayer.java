package com.group12.model;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Random;

public class CPUPlayer {
  private String color;

  public String displayName;

  private ArrayList<String> ownedCircles;

  private ArrayList<String> ownedRoads;

  private int hillResource;
  private int mountainResource;
  private int forestResource;
  private int fieldResource;
  private int pastureFieldResource;

  public CPUPlayer(String color, String displayName, ArrayList<String> ownedCircles, ArrayList<String> ownedRoads) {
    this.displayName = displayName;
    this.color = color;
    this.ownedCircles = ownedCircles;
    this.ownedRoads = ownedRoads;
    this.hillResource = 0;
    this.mountainResource = 0;
    this.forestResource = 0;
    this.fieldResource = 0;
    this.pastureFieldResource = 0;
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
          node.setScaleX(1.3);
          node.setScaleY(1.3);
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

  public ArrayList<String> getOwnedCircles() {
    return ownedCircles;
  }

  public void setOwnedCircles(ArrayList<String> ownedCircles) {
    this.ownedCircles = ownedCircles;
  }

  public ArrayList<String> getOwnedRoads() {
    return ownedRoads;
  }

  public void setOwnedRoads(ArrayList<String> ownedRoads) {
    this.ownedRoads = ownedRoads;
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
