package com.group12.controller;

import com.group12.helper.NotificationHelper;
import com.group12.model.CPUPlayer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.group12.helper.GameBoardSetupHelper.*;
import static com.group12.helper.GameHelper.*;
import static com.group12.helper.MediaHelper.diceEffect;
import static com.group12.helper.MediaHelper.playSoundEffect;

@Component
public class GameController {

  @FXML private Polygon hexagon;

  @FXML private AnchorPane anchPane;

  @FXML private Text h1;
  @FXML private Text h2;
  @FXML private Text h3;
  @FXML private Text h4;
  @FXML private Text h5;
  @FXML private Text h6;
  @FXML private Text h7;
  @FXML private Text h8;
  @FXML private Text h9;
  @FXML private Text h10;
  @FXML private Text h11;
  @FXML private Text h12;
  @FXML private Text h13;
  @FXML private Text h14;
  @FXML private Text h15;
  @FXML private Text h16;
  @FXML private Text h17;
  @FXML private Text h18;
  @FXML private Text h19;

  @FXML private Button skipTurnButton;

  @FXML private ImageView firstDiceImage;
  @FXML private ImageView secondDiceImage;

  @FXML private Button roadBuildButton;
  @FXML private Button settlementBuildButton;
  @FXML private Button settlementUpgradeButton;

  private int turn;
  public static int d1;
  public static int d2;
  private boolean isThrown;

  public static int hillResource;
  public static int mountainResource;
  public static int forestResource;
  public static int fieldResource;
  public static int pastureFieldResource;

  public static CPUPlayer cpuOrange;
  public static CPUPlayer cpuGreen;
  public static CPUPlayer cpuPink;

  public static ArrayList<Text> tileTextList = new ArrayList<>();

  public static ArrayList<String> occupiedCircles = new ArrayList<>();
  public static ArrayList<String> occupiedEdges = new ArrayList<>();

  private ArrayList<String> ownedCircles = new ArrayList<>();
  private ArrayList<String> ownedEdges = new ArrayList<>();

  public void initialize() {
    skipTurnButton.setDisable(true);
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
    turn = 0;
    hillResource = 0;
    mountainResource = 0;
    forestResource = 0;
    fieldResource = 0;
    pastureFieldResource = 0;
    tileTextList =
        new ArrayList<>(
            Arrays.asList(
                h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18,
                h19));
    setupBoardTiles(anchPane, tileTextList);
  }

  @FXML
  public void rectanglePush(MouseEvent event) {
    Rectangle eventRectangle = (Rectangle) event.getSource();
    String rectangleId = eventRectangle.getId();

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        node.setVisible(false);
        if (node.getId().equals(rectangleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
        }
      }
    }
    occupiedEdges.add(rectangleId);
    ownedEdges.add(rectangleId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new road!");
    cpuOrange = new CPUPlayer("orange", "Orange Guy", new ArrayList<>(), new ArrayList<>());
    cpuGreen = new CPUPlayer("green", "Green Guy", new ArrayList<>(), new ArrayList<>());
    cpuPink = new CPUPlayer("pink", "Pink Guy", new ArrayList<>(), new ArrayList<>());

    CPUSetup(anchPane, cpuOrange, cpuGreen, cpuPink, occupiedCircles, occupiedEdges);

    ArrayList<String> blabla = circleOptionsAtSetup(anchPane, occupiedCircles);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (blabla.contains(node.getId())) {
          node.setVisible(true);
          node.setStyle("-fx-fill: #D3D3D3;");
          node.setOnMouseClicked(this::lastCirclePush);
        }
      }
    }
  }

  @FXML
  public void throwDice(MouseEvent event) throws InterruptedException {
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
    skipTurnButton.setDisable(false);
    isThrown = true;

    d1 = rollDice(firstDiceImage);
    d2 = rollDice(secondDiceImage);
    playSoundEffect(diceEffect);

    diceThrowResourceGather(anchPane, ownedCircles);
    if (hillResource >= 1 && forestResource >= 1) {
      roadBuildButton.setDisable(false);
    }
    if (hillResource >= 1
            && forestResource >= 1
            && fieldResource >= 1
            && pastureFieldResource >= 1) {
      settlementBuildButton.setDisable(false);
    }
    if (fieldResource >= 2 && mountainResource >= 3) {
      settlementUpgradeButton.setDisable(false);
    }
  }

  @FXML
  public void showOptionalRoads() {
    ArrayList<String> optionalRoads = getOptionalRoads(anchPane, ownedEdges, ownedCircles);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (optionalRoads.contains(node.getId())) {
          node.setVisible(!node.isVisible());
        }
      }
    }
  }

  @FXML
  public void showOptionalSettlements() {
    System.out.println("showOptionalSettlements");
  }

  @FXML
  public void showOptionalUpgrades() {
    System.out.println("showOptionalUpgrades");
  }

  @FXML
  public void skipTurn() {
    isThrown = false;
    skipTurnButton.setDisable(true);
    CPUPlays(anchPane, cpuOrange, ownedCircles);
    CPUPlays(anchPane, cpuGreen, ownedCircles);
    CPUPlays(anchPane, cpuPink, ownedCircles);
    firstDiceImage.setDisable(false);
    secondDiceImage.setDisable(false);
  }

  @FXML
  public void circlePush(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();
    // Set the settlement

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        node.setVisible(false);
        if (node.getId().equals(circleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          node.setScaleX(1.3);
          node.setScaleY(1.3);
        }
      }
    }
    occupiedCircles.add(circleId);
    ownedCircles.add(circleId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new settlement!");
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle") && node.getId().contains(circleId)) {
        String[] temp = node.getId().split("-");
        if (temp[0].equals(circleId) || temp[1].equals(circleId)) {
          node.setVisible(true);
        }
      }
    }
  }

  @FXML
  public void lastCirclePush(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();
    // Set the settlement

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {

        if (node.getId().equals(circleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          node.setScaleX(1.3);
          node.setScaleY(1.3);
        }
      }
    }
    occupiedCircles.add(circleId);
    ownedCircles.add(circleId);
    List<String> styleList;
    for (String rsrc : circleNeighbours.get(circleId).split("-")) {
      for (Node node : anchPane.getChildren()) {
        if (node.getClass().getName().contains("Polygon") && node.getId().equals(rsrc)) {
          styleList = node.getStyleClass();
          if (styleList.contains("hill")) {
            hillResource += 1;
          } else if (styleList.contains("mountain")) {
            mountainResource += 1;
          } else if (styleList.contains("forest")) {
            forestResource += 1;
          } else if (styleList.contains("field")) {
            fieldResource += 1;
          } else if (styleList.contains("pastureField")) {
            pastureFieldResource += 1;
          }
        }
      }
    }

    for (Node node1 : anchPane.getChildren()) {
      if (node1.getClass().getName().contains("Circle")) {
        if (!occupiedCircles.contains(node1.getId())) {
          node1.setVisible(false);
        }
      }
    }
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new settlement!");

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (node.getId().contains(circleId)) {
          node.setVisible(true);
          node.setOnMouseClicked(this::lastRectanglePush);
        }
      }
    }
  }

  @FXML
  public void lastRectanglePush(MouseEvent event) {
    Rectangle eventRectangle = (Rectangle) event.getSource();
    String rectangleId = eventRectangle.getId();
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (node.getId().equals(rectangleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
        }
      }
    }
    occupiedEdges.add(rectangleId);
    ownedEdges.add(rectangleId);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (!occupiedEdges.contains(node.getId())) {
          node.setVisible(false);
        }
      }
    }

    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new road!");
    finilizeSetupPhase();
  }

  public void finilizeSetupPhase() {
    firstDiceImage.setDisable(false);
    secondDiceImage.setDisable(false);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (!occupiedEdges.contains(node.getId())) {
          node.setOnMouseClicked(this::buildRoad);
        }
      }
    }
  }

  @FXML
  public void buildRoad(MouseEvent event) {
    Rectangle road = (Rectangle) event.getSource();
    String roadId = road.getId();

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")
          && !occupiedEdges.contains(node.getId())) {
        if (node.getId().equals(roadId)) {
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
        } else {
          node.setVisible(false);
        }
      }
    }
    hillResource--;
    forestResource--;
    occupiedEdges.add(roadId);
    ownedEdges.add(roadId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new road!");
    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
  }

  @FXML
  public void buildRSettlement() {}

  @FXML
  public void upgradeRSettlement() {}

  @FXML
  public void hexagonPush(MouseEvent event) {
    Node node = (Polygon) event.getSource();
    System.out.println(node.getId() + ": " + node.getStyleClass());
  }
}
