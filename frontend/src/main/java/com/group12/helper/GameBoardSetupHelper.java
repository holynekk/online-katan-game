package com.group12.helper;

import com.group12.model.CPUPlayer;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.*;

import static com.group12.helper.GameHelper.*;

public class GameBoardSetupHelper {


  private static ArrayList<String> tileList =
      new ArrayList<>(
          Arrays.asList(
              "hill",
              "hill",
              "hill",
              "mountain",
              "mountain",
              "mountain",
              "forest",
              "forest",
              "forest",
              "forest",
              "field",
              "field",
              "field",
              "field",
              "pastureField",
              "pastureField",
              "pastureField",
              "pastureField"));

  private static ArrayList<String> tileResources =
      new ArrayList<>(
          Arrays.asList(
              "2", "3", "11", "12", "4", "4", "5", "5", "6", "6", "7", "7", "8", "8", "9", "9",
              "10", "10"));

  public static ArrayList<String> circleOptionsAtSetup(
      AnchorPane anchorPane, ArrayList<String> occupiedCircles) {
    ArrayList<String> circleOptionList = new ArrayList<>(circleList);
    ArrayList<String> occupiedOptionalList = new ArrayList<>(occupiedCircles);
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        String[] temp = node.getId().split("-");
        if (occupiedCircles.contains(temp[0])) {
          occupiedOptionalList.add(temp[1]);
        } else if (occupiedCircles.contains(temp[1])) {
          occupiedOptionalList.add(temp[0]);
        }
      }
    }
    circleOptionList.removeAll(occupiedOptionalList);
    return circleOptionList;
  }

  public static void setupBoardTiles(AnchorPane anchorPane, ArrayList<Text> tileTextList) {
    Random rnd = new Random();
    int rndInt;
    for (Node node : anchorPane.getChildren()) {
      if (node.getClass().getName().contains("Polygon")) {
        if (!node.getStyleClass().contains("desert")) {
          // Assign resource types
          rndInt = rnd.nextInt(tileList.size());
          node.getStyleClass().add(tileList.get(rndInt));
          tileList.remove(rndInt);
          // Assign tile resources
          rndInt = rnd.nextInt(tileResources.size());
          tileTextList
              .get(Integer.parseInt(node.getId().substring(1)) - 1)
              .setText(tileResources.get(rndInt));
          tileResources.remove(rndInt);
        }
      }
    }
  }

  public static void setResourceAtSetup(
      AnchorPane anchorPane, CPUPlayer cpuPlayer, String cpuCircleId) {
    List<String> styleList;
    for (String rsrc : circleNeighbours.get(cpuCircleId).split("-")) {
      for (Node node : anchorPane.getChildren()) {
        if (node.getClass().getName().contains("Polygon") && node.getId().equals(rsrc)) {
          styleList = node.getStyleClass();
          if (styleList.contains("hill")) {
            cpuPlayer.increaseHillResource(1);
          } else if (styleList.contains("mountain")) {
            cpuPlayer.increaseMountainResource(1);
          } else if (styleList.contains("forest")) {
            cpuPlayer.increaseForestResource(1);
          } else if (styleList.contains("field")) {
            cpuPlayer.increaseFieldResource(1);
          } else if (styleList.contains("pastureField")) {
            cpuPlayer.increasePastureFieldResource(1);
          }
        }
      }
    }
  }

  public static void CPUSetup(
      AnchorPane anchorPane,
      CPUPlayer cpuGreen,
      CPUPlayer cpuPink,
      CPUPlayer cpuOrange,
      ArrayList<String> occupiedCircles,
      ArrayList<String> occupiedEdges) {
    // CPU plays
    String cpuCircleId;
    String cpuRectangleId;
    // Orange --------------------------------------------------------------------
    cpuCircleId =
        cpuOrange.buildSettlementAtSetup(
            anchorPane, circleOptionsAtSetup(anchorPane, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuOrange.buildRoadAtSetup(anchorPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Green --------------------------------------------------------------------
    cpuCircleId =
        cpuGreen.buildSettlementAtSetup(
            anchorPane, circleOptionsAtSetup(anchorPane, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuGreen.buildRoadAtSetup(anchorPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Pink --------------------------------------------------------------------
    cpuCircleId =
        cpuPink.buildSettlementAtSetup(
            anchorPane, circleOptionsAtSetup(anchorPane, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuPink.buildRoadAtSetup(anchorPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Pink AGAIN --------------------------------------------------------------------
    cpuCircleId =
        cpuPink.buildSettlementAtSetup(
            anchorPane, circleOptionsAtSetup(anchorPane, occupiedCircles));
    occupiedCircles.add(cpuCircleId);
    setResourceAtSetup(anchorPane, cpuPink, cpuCircleId);

    cpuRectangleId = cpuPink.buildRoadAtSetup(anchorPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Green AGAIN --------------------------------------------------------------------
    cpuCircleId =
        cpuGreen.buildSettlementAtSetup(
            anchorPane, circleOptionsAtSetup(anchorPane, occupiedCircles));
    occupiedCircles.add(cpuCircleId);
    setResourceAtSetup(anchorPane, cpuGreen, cpuCircleId);

    cpuRectangleId = cpuGreen.buildRoadAtSetup(anchorPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Orange AGAIN --------------------------------------------------------------------
    cpuCircleId =
        cpuOrange.buildSettlementAtSetup(
            anchorPane, circleOptionsAtSetup(anchorPane, occupiedCircles));
    occupiedCircles.add(cpuCircleId);
    setResourceAtSetup(anchorPane, cpuOrange, cpuCircleId);

    cpuRectangleId = cpuOrange.buildRoadAtSetup(anchorPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);
  }
}
