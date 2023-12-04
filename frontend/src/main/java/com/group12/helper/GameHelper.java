package com.group12.helper;

import com.group12.model.CPUPlayer;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

import static com.group12.controller.GameController.*;

public class GameHelper {
  public static ArrayList<String> hexagonList =
      new ArrayList<>(
          Arrays.asList(
              "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "h9", "h10", "h11", "h12", "h13",
              "h14", "h15", "h16", "h17", "h18", "h19"));

  public static ArrayList<String> rectangleList =
      new ArrayList<>(
          Arrays.asList(
              "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "h9", "h10", "h11", "h12", "h13",
              "h14", "h15", "h16", "h17", "h18", "h19"));

  public static ArrayList<String> circleList =
      new ArrayList<>(
          Arrays.asList(
              "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10", "c11", "c12", "c13",
              "c14", "c15", "c16", "c17", "c18", "c19", "c20", "c21", "c22", "c23", "c24", "c25",
              "c26", "c27", "c28", "c29", "c30", "c31", "c32", "c33", "c34", "c35", "c36", "c37",
              "c38", "c39", "c40", "c41", "c42", "c43", "c44", "c45", "c46", "c47", "c48", "c49",
              "c50", "c51", "c52", "c53", "c54"));
  public static final HashMap<String, String> circleNeighbours =
      new HashMap<>() {
        {
          // First Row
          put("c1", "h1");
          put("c2", "h1");
          put("c3", "h1-h2");
          put("c4", "h2");
          put("c5", "h2-h3");
          put("c6", "h3");
          put("c7", "h3");

          // Second Row
          put("c8", "h4");
          put("c9", "h1-h4");
          put("c10", "h1-h4-h5");
          put("c11", "h1-h2-h5");
          put("c12", "h2-h5-h6");
          put("c13", "h2-h3-h6");
          put("c14", "h3-h6-h7");
          put("c15", "h3-h7");
          put("c16", "h7");

          // Third Row
          put("c17", "h8");
          put("c18", "h4-h8");
          put("c19", "h4-h8-h9");
          put("c20", "h4-h5-h9");
          put("c21", "h5-h9");
          put("c22", "h5-h6");
          put("c23", "h6-11");
          put("c24", "h6-h7-h11");
          put("c25", "h7-h11-h12");
          put("c26", "h7-h12");
          put("c27", "h12");

          // Fourth Row
          put("c28", "h8");
          put("c29", "h8-h13");
          put("c30", "h8-h9-h13");
          put("c31", "h9-h13-h14");
          put("c32", "h9-h14");
          put("c33", "h14-h15");
          put("c34", "h11-h15");
          put("c35", "h11-h15-h16");
          put("c36", "h11-h12-h16");
          put("c37", "h12-h16");
          put("c38", "h12");

          // Fifth Row
          put("c39", "h13");
          put("c40", "h13-h17");
          put("c41", "h13-h14-h17");
          put("c42", "h14-h17-h18");
          put("c43", "h14-h15-h18");
          put("c44", "h15-h18-h19");
          put("c45", "h15-h16-h19");
          put("c46", "h16-h19");
          put("c47", "h16");

          // Sixth Row
          put("c48", "h17");
          put("c49", "h17");
          put("c50", "h17-h19");
          put("c51", "h18");
          put("c52", "h18-h19");
          put("c53", "h19");
          put("c54", "h19");
        }
      };

  public static void gatherNewResourcesPlayer(
      AnchorPane anchorPane, List<Text> tileTextList, List<String> ownedCircles, int diceResult) {
    for (Text txt : tileTextList) {
      if (txt.getText().matches("-?\\d+(\\.\\d+)?")
          && Integer.parseInt(txt.getText()) == diceResult) {
        // For player
        for (String cId : ownedCircles) {
          if (circleNeighbours.get(cId).contains("-" + txt.getId() + "-")
              || circleNeighbours.get(cId).startsWith(txt.getId() + "-")
              || circleNeighbours.get(cId).endsWith("-" + txt.getId())) {
            for (Node node : anchorPane.getChildren()) {

              if (node.getClass().getName().contains("Polygon")) {
                if (node.getId().equals(txt.getId())) {
                  List<String> styleList = node.getStyleClass();
                  if (styleList.contains("hill")) {
                    hillResource++;
                  } else if (styleList.contains("mountain")) {
                    mountainResource++;
                  } else if (styleList.contains("forest")) {
                    forestResource++;
                  } else if (styleList.contains("field")) {
                    fieldResource++;
                  } else if (styleList.contains("pastureField")) {
                    pastureFieldResource++;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public static void gatherNewResourcesCPU(
      AnchorPane anchorPane,
      CPUPlayer cpuPlayer,
      List<Text> tileTextList,
      List<String> ownedCircles,
      int diceResult) {
    for (Text txt : tileTextList) {
      if (txt.getText().matches("-?\\d+(\\.\\d+)?")
          && Integer.parseInt(txt.getText()) == diceResult) {
        // For player
        for (String cId : ownedCircles) {
          if (circleNeighbours.get(cId).contains("-" + txt.getId() + "-")
              || circleNeighbours.get(cId).startsWith(txt.getId() + "-")
              || circleNeighbours.get(cId).endsWith("-" + txt.getId())) {
            for (Node node : anchorPane.getChildren()) {

              if (node.getClass().getName().contains("Polygon")) {
                if (node.getId().equals(txt.getId())) {
                  List<String> styleList = node.getStyleClass();
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
        }
      }
    }
  }

  public static void CPUPlays(
      AnchorPane anchorPane, CPUPlayer cpuPlayer, List<String> ownedCircles) {
    System.out.println(cpuPlayer.getDisplayName() + " plays!");
    diceThrowResourceGather(anchorPane, ownedCircles);

    if (cpuPlayer.getHillResource() >= 1 && cpuPlayer.getForestResource() >= 1) {
      System.out.println(cpuPlayer.getDisplayName() + " can build a road!");
    } else if (cpuPlayer.getHillResource() >= 1
        && cpuPlayer.getForestResource() >= 1
        && cpuPlayer.getFieldResource() >= 1
        && cpuPlayer.getPastureFieldResource() >= 1) {
      System.out.println(cpuPlayer.getDisplayName() + " can build a settlement!");
    } else if (cpuPlayer.getFieldResource() >= 2 && cpuPlayer.getMountainResource() >= 3) {
      System.out.println(cpuPlayer.getDisplayName() + " can upgrade a settlement to a city!");
    }
  }

  public static void diceThrowResourceGather(AnchorPane anchorPane, List<String> ownedCircles) {
    // Share resources
    gatherNewResourcesPlayer(anchorPane, tileTextList, ownedCircles, d1 + d2);
    gatherNewResourcesCPU(
        anchorPane, cpuOrange, tileTextList, cpuOrange.getOwnedCircles(), d1 + d2);
    gatherNewResourcesCPU(anchorPane, cpuGreen, tileTextList, cpuGreen.getOwnedCircles(), d1 + d2);
    gatherNewResourcesCPU(anchorPane, cpuPink, tileTextList, cpuPink.getOwnedCircles(), d1 + d2);
  }

  public static int rollDice(ImageView diceImage) {
    Random rnd = new Random();
    int diceResult = rnd.nextInt(6) + 1;
    File file = new File("src/main/resources/assets/dice" + diceResult + ".png");
    RotateTransition rt = new RotateTransition();
    rt.setByAngle(360);
    rt.setNode(diceImage);
    rt.setDuration(Duration.millis(500));
    rt.play();
    rt.setOnFinished(j -> diceImage.setImage(new Image(file.toURI().toString())));
    return diceResult;
  }

  public static ArrayList<String> getOptionalRoads(
      AnchorPane anchorPane, ArrayList<String> ownedEdges, ArrayList<String> ownedCircles) {
    ArrayList<String> optionalRoads = new ArrayList<>();

    ArrayList<String> circles = new ArrayList<>();
    for (String edge : ownedEdges) {
      String[] temp = edge.split("-");
      if (!circles.contains(temp[0])) {
        circles.add(temp[0]);
      }
      if (!circles.contains(temp[1])) {
        circles.add(temp[1]);
      }
    }
    for (String circleId : circles) {
      for (Node node : anchorPane.getChildren()) {
        if (node.getClass().getName().contains("Rectangle")) {
          String[] temp = node.getId().split("-");
          if (temp[0].equals(circleId) || temp[1].equals(circleId)) {
            optionalRoads.add(node.getId());
          }
        }
      }
    }
    optionalRoads.removeAll(occupiedEdges);
    return optionalRoads;
  }
}
