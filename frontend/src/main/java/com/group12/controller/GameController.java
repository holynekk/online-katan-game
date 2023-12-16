package com.group12.controller;

import com.group12.helper.NotificationHelper;
import com.group12.model.CPUPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.group12.helper.GameBoardSetupHelper.*;
import static com.group12.helper.GameHelper.*;
import static com.group12.helper.MediaHelper.*;

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

  @FXML private Pane p1;
  @FXML private Pane p2;
  @FXML private Pane p3;
  @FXML private Pane p4;

  @FXML private Text player1Name;
  @FXML private Text player2Name;
  @FXML private Text player3Name;
  @FXML private Text player4Name;

  @FXML private Text player1LongestRoad;
  @FXML private Text player2LongestRoad;
  @FXML private Text player3LongestRoad;
  @FXML private Text player4LongestRoad;

  @FXML private Text player1TotalResources;
  @FXML private Text player2TotalResources;
  @FXML private Text player3TotalResources;
  @FXML private Text player4TotalResources;

  @FXML private Text player1Score;
  @FXML private Text player2Score;
  @FXML private Text player3Score;
  @FXML private Text player4Score;

  @FXML private Text woolText;
  @FXML private Text oreText;
  @FXML private Text grainText;
  @FXML private Text brickText;
  @FXML private Text lumberText;

  @FXML private Label resultBanner;
  @FXML private Button backButton;

  public static int d1;
  public static int d2;
  private boolean isOver;

  public static int hillResource;
  public static int mountainResource;
  public static int forestResource;
  public static int fieldResource;
  public static int pastureFieldResource;
  public static int totalResources;

  public static CPUPlayer cpuOrange;
  public static CPUPlayer cpuGreen;
  public static CPUPlayer cpuPink;

  public static ArrayList<Text> tileTextList = new ArrayList<>();

  public static ArrayList<String> occupiedCircles = new ArrayList<>();
  public static ArrayList<String> occupiedEdges = new ArrayList<>();

  private ArrayList<String> ownedCircles = new ArrayList<>();
  private ArrayList<String> ownedCities = new ArrayList<>();
  private ArrayList<String> ownedEdges = new ArrayList<>();

  public void initialize() {
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
    isOver = false;
    hillResource = 20;
    mountainResource = 20;
    forestResource = 20;
    fieldResource = 20;
    pastureFieldResource = 20;
    totalResources = 80;
    tileTextList =
        new ArrayList<>(
            Arrays.asList(
                h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18,
                h19));
    setupBoardTiles(anchPane, tileTextList);

    cpuOrange = new CPUPlayer("orange", "Orange Guy");
    cpuGreen = new CPUPlayer("green", "Green Guy");
    cpuPink = new CPUPlayer("pink", "Pink Guy");

    player1Name.setText("bum bum");
    player2Name.setText(cpuOrange.getDisplayName());
    player3Name.setText(cpuGreen.getDisplayName());
    player4Name.setText(cpuPink.getDisplayName());

    p1.getStyleClass().addAll("player-turn");
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

    CPUSetup(anchPane, cpuOrange, cpuGreen, cpuPink, occupiedCircles, occupiedEdges);
    Timeline timeline = new Timeline();
    KeyFrame kv =
        new KeyFrame(
            Duration.seconds(7),
            e -> {
              ArrayList<String> circleOptions = circleOptionsAtSetup(anchPane);
              for (Node node : anchPane.getChildren()) {
                if (node.getClass().getName().contains("Circle")) {
                  if (circleOptions.contains(node.getId())) {
                    node.setVisible(true);
                    node.setStyle("-fx-fill: #D3D3D3;");
                    node.setOnMouseClicked(this::lastCirclePush);
                  }
                }
              }
            });
    timeline.getKeyFrames().add(kv);
    timeline.play();
  }

  @FXML
  public void throwDice(MouseEvent event) {
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
    skipTurnButton.setDisable(false);

    diceThrowResourceGather(anchPane, ownedCircles, firstDiceImage, secondDiceImage);
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
    playSoundEffect(buttonSound);
    clearAllOptionals(anchPane, ownedCircles, ownedCities);
    ArrayList<String> optionalRoads = getOptionalRoads(anchPane, ownedEdges);
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
    playSoundEffect(buttonSound);
    clearAllOptionals(anchPane, ownedCircles, ownedCities);
    ArrayList<String> optionalSettlements = getOptionalSettlements(anchPane, ownedEdges);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (optionalSettlements.contains(node.getId())) {
          node.setVisible(!node.isVisible());
        }
      }
    }
  }

  @FXML
  public void showOptionalUpgrades() {
    playSoundEffect(buttonSound);
    clearAllOptionals(anchPane, ownedCircles, ownedCities);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")
          && ownedCircles.contains(node.getId())
          && !ownedCities.contains(node.getId())) {
        node.setScaleX(1.3);
        node.setScaleY(1.3);
        node.setStyle("-fx-fill: #D3D3D3;");
        node.setOnMouseClicked(this::upgradeToCity);
        node.setCursor(Cursor.HAND);
      }
    }
  }

  @FXML
  public void skipTurn() {
    playSoundEffect(buttonSound);
    skipTurnButton.setDisable(true);
    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    clearAllOptionals(anchPane, ownedCircles, ownedCities);
    checkPlayerScore();
    p1.getStyleClass().clear();
    p2.getStyleClass().addAll("player-turn");

    Timeline timeline = new Timeline();
    KeyFrame kv1 =
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              if (!isOver) {
                diceThrowResourceGather(anchPane, ownedCircles, firstDiceImage, secondDiceImage);
                updateScores();
              }
            });

    KeyFrame kv2 =
        new KeyFrame(
            Duration.seconds(2),
            event -> {
              if (!isOver) {
                CPUPlays(anchPane, cpuOrange, ownedCircles);
                updateScores();
                p2.getStyleClass().clear();
                p3.getStyleClass().addAll("player-turn");
                checkCpuScore(cpuOrange);
              }
            });

    KeyFrame kv3 =
        new KeyFrame(
            Duration.seconds(3),
            event -> {
              if (!isOver) {
                diceThrowResourceGather(anchPane, ownedCircles, firstDiceImage, secondDiceImage);
                updateScores();
              }
            });
    KeyFrame kv4 =
        new KeyFrame(
            Duration.seconds(4),
            event -> {
              if (!isOver) {
                CPUPlays(anchPane, cpuGreen, ownedCircles);
                updateScores();
                p3.getStyleClass().clear();
                p4.getStyleClass().addAll("player-turn");
                checkCpuScore(cpuGreen);
              }
            });

    KeyFrame kv5 =
        new KeyFrame(
            Duration.seconds(5),
            event -> {
              if (!isOver) {
                diceThrowResourceGather(anchPane, ownedCircles, firstDiceImage, secondDiceImage);
                updateScores();
              }
            });
    KeyFrame kv6 =
        new KeyFrame(
            Duration.seconds(6),
            event -> {
              if (!isOver) {
                CPUPlays(anchPane, cpuPink, ownedCircles);
                updateScores();
                checkCpuScore(cpuPink);
              }
            });

    KeyFrame kv7 =
        new KeyFrame(
            Duration.seconds(7),
            event -> {
              if (!isOver) {
                firstDiceImage.setDisable(false);
                secondDiceImage.setDisable(false);
                playSoundEffect(turnSound);
                updateScores();
                p4.getStyleClass().clear();
                p1.getStyleClass().addAll("player-turn");
                NotificationHelper.showAlert(
                    Alert.AlertType.INFORMATION, "Information", "It's your turn!");
              }
            });
    timeline.getKeyFrames().addAll(kv1, kv2, kv3, kv4, kv5, kv6, kv7);
    timeline.play();
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
    updateScores();
  }

  @FXML
  public void lastCirclePush(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {

        if (node.getId().equals(circleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
        }
      }
    }
    occupiedCircles.add(circleId);
    ownedCircles.add(circleId);
    List<String> styleList;
    for (String cID : circleNeighbours.get(circleId).split("-")) {
      for (Node node : anchPane.getChildren()) {
        if (node.getClass().getName().contains("Polygon") && node.getId().equals(cID)) {
          styleList = node.getStyleClass();
          totalResources += 1;
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
    updateScores();
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
    finalizeSetupPhase();
  }

  public void finalizeSetupPhase() {
    firstDiceImage.setDisable(false);
    secondDiceImage.setDisable(false);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (!occupiedEdges.contains(node.getId())) {
          node.setOnMouseClicked(this::buildRoad);
        }
      }
      if (node.getClass().getName().contains("Circle")) {
        if (!occupiedCircles.contains(node.getId())) {
          node.setOnMouseClicked(this::buildSettlement);
        }
      }
    }
    playSoundEffect(turnSound);
    NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Information", "It's your turn!");
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
    updateScores();
  }

  @FXML
  public void buildSettlement(MouseEvent event) {
    Circle settlement = (Circle) event.getSource();
    String settlementId = settlement.getId();

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle") && !occupiedCircles.contains(node.getId())) {
        if (node.getId().equals(settlementId)) {
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
    fieldResource--;
    pastureFieldResource--;
    occupiedCircles.add(settlementId);
    ownedCircles.add(settlementId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new settlement!");
    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    updateScores();
  }

  @FXML
  public void upgradeToCity(MouseEvent event) {
    Circle settlement = (Circle) event.getSource();
    String settlementId = settlement.getId();

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle") && ownedCircles.contains(node.getId())) {
        node.setStyle("-fx-fill: red;");
        node.setOnMouseClicked(null);
        node.setCursor(Cursor.DEFAULT);
        if (!node.getId().equals(settlementId)) {
          node.setScaleX(1);
          node.setScaleY(1);
        }
      }
    }
    fieldResource -= 2;
    mountainResource -= 3;
    ownedCities.add(settlementId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have upgraded your settlement to a city!");
    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    updateScores();
  }

  public void updateScores() {
    woolText.setText(Integer.toString(pastureFieldResource));
    oreText.setText(Integer.toString(mountainResource));
    grainText.setText(Integer.toString(fieldResource));
    brickText.setText(Integer.toString(hillResource));
    lumberText.setText(Integer.toString(forestResource));
    player1Score.setText(Integer.toString(ownedCircles.size() + ownedCities.size()));
    player1TotalResources.setText(
        Integer.toString(
            pastureFieldResource
                + mountainResource
                + fieldResource
                + hillResource
                + forestResource));
    player2TotalResources.setText(Integer.toString(cpuOrange.getTotalResources()));
    player2Score.setText(Integer.toString(cpuOrange.getScore()));
    player3TotalResources.setText(Integer.toString(cpuGreen.getTotalResources()));
    player3Score.setText(Integer.toString(cpuGreen.getScore()));
    player4TotalResources.setText(Integer.toString(cpuPink.getTotalResources()));
    player4Score.setText(Integer.toString(cpuPink.getScore()));
  }

  public void checkCpuScore(CPUPlayer cpuPlayer) {
    if (cpuPlayer.getScore() >= 8) {
      closeGame();
      resultBanner.setText("You have been defeated!");
      playSoundEffect(defeatedSound);
      isOver = true;
    }
  }

  public void checkPlayerScore() {
    if (ownedCities.size() + ownedCircles.size() >= 8) {
      closeGame();
      playSoundEffect(victoriousSound);
      resultBanner.setText("You are victorious!");
      isOver = true;
    }
  }

  public void closeGame() {
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
    resultBanner.setVisible(true);
    backButton.setVisible(true);
  }

  @FXML
  public void backToMenu() throws IOException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
