package com.group12.helper;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
      AnchorPane anchorPane, ArrayList<String> circleList, ArrayList<String> occupiedCircles) {
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
}
