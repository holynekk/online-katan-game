package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group12.helper.NotificationHelper;
import com.group12.helper.StompClient;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.MediaHelper.*;
import static com.group12.helper.OnlineGameHelper.*;

@Component
public class OnlineGameController {

  private Scene gameScene;

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

  private ArrayList<Text> tileTextList = new ArrayList<>();

  @FXML private Text brickText;
  @FXML private Text lumberText;
  @FXML private Text oreText;
  @FXML private Text grainText;
  @FXML private Text woolText;

  @FXML private ImageView firstDiceImage;
  @FXML private ImageView secondDiceImage;

  @FXML private Button roadBuildButton;
  @FXML private Button settlementBuildButton;
  @FXML private Button settlementUpgradeButton;
  @FXML private Button skipTurnButton;

  @FXML private Label resultBanner;
  @FXML private Button backButton;

  private StompClient stompClient;
  private String clientUsername;
  private String userColor;

  @FXML private TextField chatTextField;
  @FXML private Button sendButton;
  @FXML private ScrollPane chatScrollPane;
  @FXML private VBox chatBox;

  @FXML private VBox playerInfoBox;

  public ArrayList<String> occupiedCircles = new ArrayList<>();
  public ArrayList<String> occupiedEdges = new ArrayList<>();

  private ArrayList<String> ownedCircles = new ArrayList<>();
  private ArrayList<String> ownedCities = new ArrayList<>();
  private ArrayList<String> ownedEdges = new ArrayList<>();

  private int brickResource = 0;
  private int lumberResource = 0;
  private int oreResource = 0;
  private int grainResource = 0;
  private int woolResource = 0;

  public void initData(StompClient stompClient, String color, String[] playerUsernameList) {
    this.stompClient = stompClient;
    this.userColor = color;

    for (String username : playerUsernameList) {
      VBox vbox = new VBox();
      vbox.setAlignment(Pos.CENTER);
      vbox.setPrefHeight(60);
      vbox.setPrefWidth(195);
      vbox.setId(username);

      Label usernameLabel = new Label(username);
      usernameLabel.setTextFill(Color.WHITE);

      HBox hbox = new HBox();
      hbox.setAlignment(Pos.CENTER);
      Label totalResource = new Label("0");
      totalResource.setId(username + "Resource");
      usernameLabel.setTextFill(Color.WHITE);
      hbox.getChildren().add(totalResource);

      vbox.getChildren().add(usernameLabel);
      vbox.getChildren().add(hbox);

      playerInfoBox.getChildren().add(vbox);
    }
    playerInfoBox.getChildren().get(1).setStyle("-fx-fill: #D3D3D3;");
  }

  public void initialize() {
    gameScene = anchPane.getScene();
    clientUsername = getSessionCookie("username");
    chatTextField.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            try {
              sendChatMessage();
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
          }
        });
    tileTextList =
        new ArrayList<>(
            Arrays.asList(
                h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18,
                h19));
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
  }

  @FXML
  public void showOptionalSettlements() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    showSettlementOptions(anchPane, occupiedCircles);
  }

  @FXML
  public void showOptionalRoads() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    showRoadOptions(anchPane, ownedCircles, ownedEdges, occupiedEdges);
  }

  public void showOptionalRoadsAtSetup() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    showRoadOptions(anchPane, ownedCircles.get(ownedCircles.size() - 1));
  }

  @FXML
  public void showOptionalUpgrades() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
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
  public void skipTurn() throws JsonProcessingException {
    Message msg = new Message(MessageType.SKIP_TURN, "Now", clientUsername, "skip_turn!");
    stompClient.sendCommand(msg);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    toggleOffButtons();
  }

  @FXML
  public void throwDice(MouseEvent event) throws JsonProcessingException {
    Message msg =
        new Message(MessageType.THROW_DICE, "Now", clientUsername, "Dice has been thrown!");
    stompClient.sendCommand(msg);
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
  }

  public void diceThrowAnimation(String turnUsername, int firstDiceResult, int secondDiceResult) {
    rollDice(firstDiceImage, firstDiceResult);
    rollDice(secondDiceImage, secondDiceResult);
    playSoundEffect(diceEffect);
    if (turnUsername.equals(this.clientUsername)) {
      toggleOnButtons();
    }
  }

  public void gatherNewResources(int diceResult) throws JsonProcessingException {
    if (diceResult == 7) {
      return;
    }
    int totalGain = 0;
    for (Text txt : tileTextList) {
      if (txt.getText().matches("-?\\d+(\\.\\d+)?")
          && Integer.parseInt(txt.getText()) == diceResult) {
        for (String cId : ownedCircles) {
          if (circleNeighbours.get(cId).contains("-" + txt.getId() + "-")
              || circleNeighbours.get(cId).startsWith(txt.getId() + "-")
              || circleNeighbours.get(cId).endsWith("-" + txt.getId())) {
            for (Node node : anchPane.getChildren()) {

              if (node.getClass().getName().contains("Polygon")) {
                if (node.getId().equals(txt.getId())) {
                  List<String> styleList = node.getStyleClass();
                  if (styleList.contains("hill")) {
                    brickResource++;
                    totalGain++;
                  } else if (styleList.contains("mountain")) {
                    oreResource++;
                    totalGain++;
                  } else if (styleList.contains("forest")) {
                    lumberResource++;
                    totalGain++;
                  } else if (styleList.contains("field")) {
                    grainResource++;
                    totalGain++;
                  } else if (styleList.contains("pastureField")) {
                    woolResource++;
                    totalGain++;
                  }
                }
              }
            }
          }
        }
      }
    }

    brickText.setText(Integer.toString(brickResource));
    oreText.setText(Integer.toString(oreResource));
    lumberText.setText(Integer.toString(lumberResource));
    grainText.setText(Integer.toString(grainResource));
    woolText.setText(Integer.toString(woolResource));
    Message msg =
        new Message(
            MessageType.RESOURCE_CHANGE, "Now", this.clientUsername, Integer.toString(totalGain));
    stompClient.sendCommand(msg);
  }

  public void setPlayerResourceInfoPanel(String username, String total) {
    Label text = (Label) anchPane.getScene().lookup("#" + username + "Resource");
    text.setText(Integer.toString(Integer.parseInt(text.getText()) + Integer.parseInt(total)));
  }

  public void rollDice(ImageView diceImage, int diceResult) {
    File file = new File("src/main/resources/assets/dice" + diceResult + ".png");
    RotateTransition rt = new RotateTransition();
    rt.setByAngle(360);
    rt.setNode(diceImage);
    rt.setDuration(Duration.millis(500));
    rt.play();
    rt.setOnFinished(j -> diceImage.setImage(new Image(file.toURI().toString())));
  }

  public void setBoard(String boardData) {
    int dataIndex = 0;
    String[] boardTiles = boardData.split("/");
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Polygon")) {
        String[] data = boardTiles[dataIndex].split("-");
        if (!node.getStyleClass().contains("desert")) {
          node.getStyleClass().add(data[0]);
          tileTextList.get(Integer.parseInt(node.getId().substring(1)) - 1).setText(data[1]);
          dataIndex++;
        }
      }
    }
  }

  public void setupHelper(String turnUsername) {
    if (turnUsername.equals(this.clientUsername)) {
      showSettlementOptions(anchPane, occupiedCircles);
    }
  }

  public void turnHelper(String turnUsername) {
    if (turnUsername.equals(this.clientUsername)) {
      playSoundEffect(turnSound);
      firstDiceImage.setDisable(false);
      secondDiceImage.setDisable(false);
      NotificationHelper.showAlert(
          Alert.AlertType.INFORMATION, "Information", "It's your turn! Roll the dice.");
    }
  }

  public void toggleOnButtons() {
    if (brickResource >= 1 && lumberResource >= 1) {
      roadBuildButton.setDisable(false);
    }
    if (brickResource >= 1 && lumberResource >= 1 && grainResource >= 1 && woolResource >= 1) {
      settlementBuildButton.setDisable(false);
    }
    if (grainResource >= 2 && oreResource >= 3) {
      roadBuildButton.setDisable(false);
    }
    skipTurnButton.setDisable(false);
  }

  public void toggleOffButtons() {
    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    skipTurnButton.setDisable(true);
  }

  @FXML
  public void buildSettlement(MouseEvent event) throws JsonProcessingException {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();

    ownedCircles.add(circleId);
    Message msg = new Message(MessageType.BUILD_SETTLEMENT, "Now", clientUsername, circleId);
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);

    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
  }

  public void settlementBuilt(Message msg) {
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (node.getId().equals(msg.getContent())) {
          node.setVisible(true);
          node.setStyle(String.format("-fx-fill: %s;", msg.getUserColor()));
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          break;
        }
      }
    }
    occupiedCircles.add(msg.getContent());
  }

  @FXML
  public void buildRoad(MouseEvent event) throws JsonProcessingException {
    Rectangle eventCircle = (Rectangle) event.getSource();
    String rectangleId = eventCircle.getId();

    ownedEdges.add(rectangleId);

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);

    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);

    Message msg = new Message(MessageType.BUILD_ROAD, "Now", clientUsername, rectangleId);
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);
  }

  @FXML
  public void roadBuilt(Message msg) {
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (node.getId().equals(msg.getContent())) {
          node.setVisible(true);
          node.setStyle(String.format("-fx-fill: %s;", msg.getUserColor()));
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          break;
        }
      }
    }
    occupiedEdges.add(msg.getContent());
  }

  @FXML
  public void upgradeToCity(MouseEvent event) {}

  public void highlightPlayerInfoBox(String username) {
    for (Node node : playerInfoBox.getChildren()) {
      if (node.getClass().getName().contains("VBox")) {
        if (node.getId().equals(username)) {
          node.setStyle("-fx-background-color: D3D3D3;");
        } else {
          node.setStyle("-fx-background-color: transparent;");
        }
      }
    }
  }

  @FXML
  public void sendChatMessage() throws JsonProcessingException {
    Message msg =
        new Message(MessageType.IN_GAME_CHAT, "Now", clientUsername, chatTextField.getText());
    chatTextField.setText("");
    stompClient.sendChatMessage(msg);
  }

  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Label(message));
    chatScrollPane.setVvalue(1D);
  }

  public String getClientUsername() {
    return this.clientUsername;
  }
}
