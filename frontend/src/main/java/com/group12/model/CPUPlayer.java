package com.group12.model;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Random;

public class CPUPlayer {
  private String color;

  private ArrayList<String> ownedCircles;

  private ArrayList<String> ownedRoads;

  public CPUPlayer(String color, ArrayList<String> ownedCircles, ArrayList<String> ownedRoads) {
    this.color = color;
    this.ownedCircles = ownedCircles;
    this.ownedRoads = ownedRoads;
  }

  public String buildSettlementAtSetup(AnchorPane anchorPane, ArrayList<String> optionalSettlements) {
    Random rnd = new Random();
    int rndInt = rnd.nextInt(optionalSettlements.size());
    String circleId = optionalSettlements.get(rndInt);
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (node.getId().equals(optionalSettlements.get(rndInt))) {
          node.setVisible(true);
          node.setStyle(String.format("-fx-fill: %s;", this.color));
          node.setOnMouseClicked(null);
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
        }
      }
    }
    return roadId;
  }
}
