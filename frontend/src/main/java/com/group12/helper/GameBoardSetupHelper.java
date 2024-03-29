package com.group12.helper;

import com.group12.model.CPUPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

import static com.group12.controller.GameController.occupiedCircles;
import static com.group12.helper.GameHelper.*;

/**
 * The {@code GameBoardSetupHelper} class is responsible for having helper methods about setting up
 * the board for offline (against CPU) games.
 */
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
              "2", "12", "3", "3", "4", "4", "5", "5", "6", "6", "8", "8", "9", "9", "10", "10",
              "11", "11"));

  /**
   * A method to return optional settlement positions at the setup phase.
   *
   * @param anchorPane - Main pane of the gameView scene.
   * @return - List of optional settlement places.
   */
  public static ArrayList<String> circleOptionsAtSetup(AnchorPane anchorPane) {
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

  /**
   * A method to set attributes of the hexagonal tiles of the map. Randomizes the numbers and
   * resource types for the tiles.
   *
   * @param anchorPane - Main pane of the gameView scene.
   * @param tileTextList - A list of all hexagonal tiles' list.
   */
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

  /**
   * A helper method for cpu to gather resources at the second turn of the setup phase.
   *
   * @param anchorPane - Main pane of the gameView scene.
   * @param cpuPlayer - The cpu player which is playing.
   * @param cpuCircleId - Circle id which cpu player has built its settlement on.
   */
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

  /**
   * A method to allow CPU players to play in order while in setup phase.
   *
   * @param anchorPane
   * @param cpuOrange - Orange cpu player
   * @param cpuGreen - Green cpu player
   * @param cpuPink - Pink cpu player
   * @param occupiedCircles - Settlement positions that are already occupied by other players.
   * @param occupiedEdges - Road positions that are already occupied by other players.
   */
  public static void CPUSetup(
      AnchorPane anchorPane,
      CPUPlayer cpuOrange,
      CPUPlayer cpuGreen,
      CPUPlayer cpuPink,
      ArrayList<String> occupiedCircles,
      ArrayList<String> occupiedEdges) {
    Timeline timeline = new Timeline();

    KeyFrame kv1 =
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              String cpuCircleId;
              String cpuRectangleId;
              // Orange --------------------------------------------------------------------
              cpuCircleId =
                  cpuOrange.buildSettlementAtSetup(anchorPane, circleOptionsAtSetup(anchorPane));
              occupiedCircles.add(cpuCircleId);

              cpuRectangleId = cpuOrange.buildRoadAtSetup(anchorPane, cpuCircleId);
              occupiedEdges.add(cpuRectangleId);
            });

    KeyFrame kv2 =
        new KeyFrame(
            Duration.seconds(2),
            event -> {
              String cpuCircleId;
              String cpuRectangleId;
              // Green --------------------------------------------------------------------
              cpuCircleId =
                  cpuGreen.buildSettlementAtSetup(anchorPane, circleOptionsAtSetup(anchorPane));
              occupiedCircles.add(cpuCircleId);

              cpuRectangleId = cpuGreen.buildRoadAtSetup(anchorPane, cpuCircleId);
              occupiedEdges.add(cpuRectangleId);
            });

    KeyFrame kv3 =
        new KeyFrame(
            Duration.seconds(3),
            event -> {
              String cpuCircleId;
              String cpuRectangleId;
              // Pink --------------------------------------------------------------------
              cpuCircleId =
                  cpuPink.buildSettlementAtSetup(anchorPane, circleOptionsAtSetup(anchorPane));
              occupiedCircles.add(cpuCircleId);

              cpuRectangleId = cpuPink.buildRoadAtSetup(anchorPane, cpuCircleId);
              occupiedEdges.add(cpuRectangleId);
            });

    KeyFrame kv4 =
        new KeyFrame(
            Duration.seconds(4),
            event -> {
              String cpuCircleId;
              String cpuRectangleId;
              // Pink AGAIN --------------------------------------------------------------------
              cpuCircleId =
                  cpuPink.buildSettlementAtSetup(anchorPane, circleOptionsAtSetup(anchorPane));
              occupiedCircles.add(cpuCircleId);
              setResourceAtSetup(anchorPane, cpuPink, cpuCircleId);

              cpuRectangleId = cpuPink.buildRoadAtSetup(anchorPane, cpuCircleId);
              occupiedEdges.add(cpuRectangleId);
            });

    KeyFrame kv5 =
        new KeyFrame(
            Duration.seconds(5),
            event -> {
              String cpuCircleId;
              String cpuRectangleId;
              // Green AGAIN --------------------------------------------------------------------
              cpuCircleId =
                  cpuGreen.buildSettlementAtSetup(anchorPane, circleOptionsAtSetup(anchorPane));
              occupiedCircles.add(cpuCircleId);
              setResourceAtSetup(anchorPane, cpuGreen, cpuCircleId);

              cpuRectangleId = cpuGreen.buildRoadAtSetup(anchorPane, cpuCircleId);
              occupiedEdges.add(cpuRectangleId);
            });

    KeyFrame kv6 =
        new KeyFrame(
            Duration.seconds(6),
            event -> {
              String cpuCircleId;
              String cpuRectangleId;
              // Orange AGAIN --------------------------------------------------------------------
              cpuCircleId =
                  cpuOrange.buildSettlementAtSetup(anchorPane, circleOptionsAtSetup(anchorPane));
              occupiedCircles.add(cpuCircleId);
              setResourceAtSetup(anchorPane, cpuOrange, cpuCircleId);

              cpuRectangleId = cpuOrange.buildRoadAtSetup(anchorPane, cpuCircleId);
              occupiedEdges.add(cpuRectangleId);
            });
    timeline.getKeyFrames().addAll(kv1, kv2, kv3, kv4, kv5, kv6);
    timeline.play();
  }
}
