package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.helper.NotificationHelper;
import com.group12.helper.StompClient;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.group12.helper.GameHelper.circleNeighbours;
import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.MediaHelper.*;
import static com.group12.helper.OnlineGameHelper.*;

@Component
public class OnlineGameController {

  private Scene gameScene;

  private String gameId;

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
  @FXML private Button tradeButton;
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

  @FXML private AnchorPane tradePanel;
  @FXML private ToggleGroup resourceGiveGroup;
  @FXML private ToggleGroup resourceGetGroup;
  @FXML private Label tradeUsername;
  @FXML private Rectangle tradeGivenResource;
  @FXML private Rectangle tradeWantedResource;
  @FXML private Button tradeAcceptButton;
  @FXML private Button tradeRejectButton;
  @FXML private Button sendTradeButton;
  @FXML private Button cancelTradeButton;
  @FXML private AnchorPane tradeOfferPanel;

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

  private int score = 0;
  private int longestRoad = 0;
  private boolean haveLongestRoad = false;

  private boolean gameEndedAlready = false;

  /**
   * A controller method to initialize some data about the game at the very beginning.
   *
   * @param stompClient - Stomp client which will be used to send game commands through websocket
   *     connection.
   * @param color - Client's color code to initialize it's color on the board.
   * @param playerUsernameList - List of players usernames in the lobby.
   * @param userColorList - List of players colors in the lobby.
   * @param gameId - Current game instance's id.
   */
  public void initData(
      StompClient stompClient,
      String color,
      String[] playerUsernameList,
      String[] userColorList,
      String gameId) {
    this.stompClient = stompClient;
    this.userColor = color;
    this.gameId = gameId;

    for (int i = 0; i < playerUsernameList.length; i++) {
      String username = playerUsernameList[i];
      String panelColor = userColorList[i];
      VBox vbox = new VBox();
      vbox.setAlignment(Pos.CENTER);
      vbox.setPrefHeight(60);
      vbox.setPrefWidth(195);
      vbox.setId(username);

      Label usernameLabel = new Label(username);
      usernameLabel.setTextFill(Color.WHITE);
      usernameLabel.setAlignment(Pos.CENTER);
      usernameLabel.setStyle(
          String.format(
              "-fx-font-weight: bold; -fx-border-width: 2 0 2 0; -fx-border-color: %s;",
              panelColor));
      usernameLabel.setPrefWidth(195);

      HBox hbox = new HBox(15);
      hbox.setAlignment(Pos.CENTER);

      AnchorPane resourceBox = new AnchorPane();
      resourceBox.setStyle("-fx-background-color: rgb(50, 50, 50, 0.9); -fx-background-radius: 5;");
      resourceBox.setPrefWidth(20);
      resourceBox.setPrefHeight(15);

      Label label1 = new Label("?");
      label1.setTextFill(Color.WHITE);
      label1.setLayoutX(9);
      label1.setLayoutY(12);
      resourceBox.getChildren().add(label1);

      Label label2 = new Label("0");
      label2.setTextFill(Color.WHITE);
      label2.setId(username + "Resource");
      label2.setLayoutX(15);
      resourceBox.getChildren().add(label2);
      hbox.getChildren().add(resourceBox);

      Label label4 = new Label("0");
      label4.setTextFill(Color.WHITE);
      label4.setId(username + "LongestRoad");
      label4.setStyle(String.format("-fx-border-width: 0 0 3 0; -fx-border-color: %s", panelColor));
      hbox.getChildren().add(label4);

      Label label5 = new Label("Score: ");
      label5.setTextFill(Color.WHITE);
      hbox.getChildren().add(label5);

      Label label6 = new Label("0");
      label6.setTextFill(Color.WHITE);
      label6.setId(username + "Score");
      hbox.getChildren().add(label6);

      vbox.getChildren().add(usernameLabel);
      vbox.getChildren().add(hbox);

      playerInfoBox.getChildren().add(vbox);
    }
    playerInfoBox
        .getChildren()
        .get(1)
        .setStyle(
            "-fx-background-color: D3D3D3; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0); -fx-background-color: #bc9d7e;");
  }

  /** A method to set and initialize some data (setting client username, background image, etc.) */
  public void initialize() {
    gameScene = anchPane.getScene();
    switchBackgroundMusic(true);
    this.clientUsername = getSessionCookie("username");
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

  /** A button action to show optional settlements. */
  @FXML
  public void showOptionalSettlements() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    showSettlementOptions(anchPane, occupiedCircles, ownedEdges);
  }

  /** A button action to show optional roads. */
  @FXML
  public void showOptionalRoads() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    showRoadOptions(anchPane, ownedCircles, ownedEdges, occupiedEdges);
  }

  /** A button action to show optional roads specifically for the setup phase. */
  public void showOptionalRoadsAtSetup() {
    playSoundEffect(buttonSound);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    showRoadOptions(anchPane, ownedCircles.get(ownedCircles.size() - 1));
  }

  /** A button action to show optional upgrades from a settlement to a city. */
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
        node.setStyle("-fx-fill: #808080;");
        node.setOnMouseClicked(this::upgradeSettlement);
        node.setCursor(Cursor.HAND);
      }
    }
  }

  /**
   * A button action to skip turn.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void skipTurn() throws JsonProcessingException {
    Message msg = new Message(MessageType.SKIP_TURN, "Now", clientUsername, "skip_turn!");
    stompClient.sendCommand(msg);
    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    toggleOffButtons(true);
    removeResourceTradeType(tradeGivenResource, tradeWantedResource);
    playSoundEffect(buttonSound);
  }

  /**
   * A button action to show optional settlements.
   *
   * @param event - Mouse event when the image get clicked.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void throwDice(MouseEvent event) throws JsonProcessingException {
    Message msg =
        new Message(MessageType.THROW_DICE, "Now", clientUsername, "Dice has been thrown!");
    stompClient.sendCommand(msg);
    firstDiceImage.setDisable(true);
    secondDiceImage.setDisable(true);
  }

  /**
   * A method to run dice rolling animation.
   *
   * @param turnUsername - Current turn's username.
   * @param firstDiceResult - First dice result
   * @param secondDiceResult - Second dice result
   */
  public void diceThrowAnimation(String turnUsername, int firstDiceResult, int secondDiceResult) {
    rollDice(firstDiceImage, firstDiceResult);
    rollDice(secondDiceImage, secondDiceResult);
    playSoundEffect(diceEffect);
    if (turnUsername.equals(this.clientUsername)) {
      toggleOnButtons();
    }
  }

  /**
   * A method to gather new resources from the tiles that are adjacent to the owned
   * settlements/cities.
   *
   * @param diceResult - Sum of first and the second dice results.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
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
    updateResourcePanels(totalGain);
  }

  /**
   * A method to gather resources at the second turn of the setup phase.
   *
   * @param circleId - Settlement that is built in the setup phase
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  public void gatherResourcesAtSetup(String circleId) throws JsonProcessingException {
    List<String> styleList;
    int totalGain = 0;
    for (String resourceTile : circleNeighbours.get(circleId).split("-")) {
      for (Node node : anchPane.getChildren()) {
        if (node.getClass().getName().contains("Polygon") && node.getId().equals(resourceTile)) {
          styleList = node.getStyleClass();
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
    updateResourcePanels(totalGain);
  }

  /**
   * A method to update resource panels after gathering or losing resources.
   *
   * @param totalGain - Total gain or lose of resources.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  public void updateResourcePanels(int totalGain) throws JsonProcessingException {
    refreshClientResources();
    Message msg =
        new Message(
            MessageType.RESOURCE_CHANGE,
            "Now",
            this.clientUsername,
            totalGain
                + "/"
                + longestRoad
                + "/"
                + (score + (this.haveLongestRoad && this.longestRoad >= 5 ? 2 : 0)));
    stompClient.sendCommand(msg);
  }

  /** A method to refresh resources board at the bottom of the scene. */
  public void refreshClientResources() {
    brickText.setText(Integer.toString(brickResource));
    oreText.setText(Integer.toString(oreResource));
    lumberText.setText(Integer.toString(lumberResource));
    grainText.setText(Integer.toString(grainResource));
    woolText.setText(Integer.toString(woolResource));
  }

  /**
   * A method to refresh score board on the right side of the scene.
   *
   * @throws IOException - Exception while sending the message.
   * @throws InterruptedException - Exception while sending the message.
   */
  public void updateScorePanel() throws IOException, InterruptedException {
    Message msg =
        new Message(
            MessageType.RESOURCE_CHANGE,
            "Now",
            this.clientUsername,
            "0/"
                + this.longestRoad
                + "/"
                + (score + (this.haveLongestRoad && this.longestRoad >= 5 ? 2 : 0)));
    stompClient.sendCommand(msg);
    checkScore();
  }

  /**
   * A method to update user resources related information on the right panel.
   *
   * @param username - Player username whose resources will be updated.
   * @param msgContent - Message content.
   */
  public void setPlayerResourceInfoPanel(String username, String msgContent) {
    String[] playerInfo = msgContent.split("/");

    Label text = (Label) anchPane.getScene().lookup("#" + username + "Resource");
    text.setText(
        Integer.toString(Integer.parseInt(text.getText()) + Integer.parseInt(playerInfo[0])));
  }

  /**
   * A method to update user score related information on the right panel.
   *
   * @param username - Player username whose resources will be updated.
   * @param msgContent - Message content.
   */
  public void setPlayerScorePanel(String username, String msgContent) {
    String[] playerInfo = msgContent.split("/");

    Label text = (Label) anchPane.getScene().lookup("#" + username + "LongestRoad");
    text.setText(Integer.toString(Integer.parseInt(playerInfo[1])));

    text = (Label) anchPane.getScene().lookup("#" + username + "Score");
    text.setText(Integer.toString(Integer.parseInt(playerInfo[2])));
  }

  /**
   * A method to roll the dice for all players at the same time.
   *
   * @param diceImage - Dice image which is clicked on.
   * @param diceResult - Sum of dice result one and two.
   */
  public void rollDice(ImageView diceImage, int diceResult) {
    File file = new File("src/main/resources/assets/dice" + diceResult + ".png");
    RotateTransition rt = new RotateTransition();
    rt.setByAngle(360);
    rt.setNode(diceImage);
    rt.setDuration(Duration.millis(500));
    rt.play();
    rt.setOnFinished(j -> diceImage.setImage(new Image(file.toURI().toString())));
  }

  /**
   * A method to set the board tiles with random resource types and numbers.
   *
   * @param boardData - Board data which comes from the websocket server.
   */
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

  /**
   * Setup phase skip turn helper.
   *
   * @param turnUsername - Current turn's username.
   */
  public void setupHelper(String turnUsername) {
    if (turnUsername.equals(this.clientUsername)) {
      playSoundEffect(turnSound);
      showSettlementOptionsAtSetup(anchPane, occupiedCircles);
    }
  }

  /**
   * Skip turn helper function to handle button toggle actions, panel on/off etc.
   *
   * @param turnUsername - Current turn's username.
   */
  public void turnHelper(String turnUsername) {
    closeTradePanels();
    closeOfferPanel();
    if (turnUsername.equals(this.clientUsername)) {
      playSoundEffect(turnSound);
      firstDiceImage.setDisable(false);
      secondDiceImage.setDisable(false);
      NotificationHelper.showAlert(
          Alert.AlertType.INFORMATION, "Information", "It's your turn! Roll the dice.");
    }
  }

  /** A method to toggle on buttons according to resource constraints. */
  public void toggleOnButtons() {
    ArrayList<String> optionalSettlements =
        getSettlementOptions(anchPane, occupiedCircles, ownedEdges);
    if (brickResource >= 1 && lumberResource >= 1) {
      roadBuildButton.setDisable(false);
    }
    if (brickResource >= 1
        && lumberResource >= 1
        && grainResource >= 1
        && woolResource >= 1
        && !optionalSettlements.isEmpty()) {
      settlementBuildButton.setDisable(false);
    }
    if (grainResource >= 2 && oreResource >= 3 && ownedCities.size() < ownedCircles.size()) {
      settlementUpgradeButton.setDisable(false);
    }
    tradeButton.setDisable(false);
    skipTurnButton.setDisable(false);
  }

  /**
   * A method to toggle off all the functional button in the game interface.
   *
   * @param isAll - Boolean value to decide if all the button should be turned off including
   *     skipTurn.
   */
  public void toggleOffButtons(Boolean isAll) {
    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    tradeButton.setDisable(true);
    skipTurnButton.setDisable(isAll);
  }

  /**
   * A method to build a settlement.
   *
   * @param event - JavaFX mouse event (clicking on the optional circles).
   */
  @FXML
  public void buildSettlement(MouseEvent event) throws IOException, InterruptedException {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    tradeButton.setDisable(true);

    if (ownedEdges.size() >= 2) {
      brickResource--;
      lumberResource--;
      grainResource--;
      woolResource--;
      updateResourcePanels(-4);
    }
    score++;
    updateScorePanel();

    ownedCircles.add(circleId);

    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);

    Message msg = new Message(MessageType.BUILD_SETTLEMENT, "Now", clientUsername, circleId);
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);
    checkScore();
    playSoundEffect(buildSound);
  }

  /**
   * A method to build settlements and visually represent it on the board for all players.
   *
   * @param msg - Message instance which contains the built settlement's information.
   */
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

  /**
   * A method to build a road.
   *
   * @param event - JavaFX mouse event (clicking on the optional rectangle).
   */
  @FXML
  public void buildRoad(MouseEvent event) throws IOException, InterruptedException {
    Rectangle eventCircle = (Rectangle) event.getSource();
    String rectangleId = eventCircle.getId();

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    tradeButton.setDisable(true);

    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);

    if (ownedEdges.size() >= 2) {
      brickResource--;
      lumberResource--;
      updateResourcePanels(-2);
    }

    ownedEdges.add(rectangleId);
    longestRoad = findLongestRoadLength(ownedEdges);
    updateScorePanel();

    Message msg = new Message(MessageType.BUILD_ROAD, "Now", clientUsername, rectangleId);
    msg.setLongestRoadLength(longestRoad);
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);
    playSoundEffect(buildSound);
  }

  /**
   * A method to build road and visually represent it on the board for all players.
   *
   * @param msg - Message instance which contains the built road's information.
   */
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
    if (!msg.getAtSetup()) {
      this.haveLongestRoad = msg.getUserWithLongestRoad().equals(this.clientUsername);
    }
    try {
      updateScorePanel();
    } catch (Exception e) {
      e.printStackTrace();
    }

    occupiedEdges.add(msg.getContent());
  }

  /**
   * A method to upgrade settlement to a city.
   *
   * @param event - JavaFX mouse event (clicking on the optional circle).
   */
  @FXML
  public void upgradeSettlement(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();

    ownedCities.add(circleId);

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);
    tradeButton.setDisable(true);

    clearOptionals(
        anchPane, ownedCircles, ownedCities, occupiedCircles, occupiedEdges, this.userColor);
    try {
      grainResource -= 2;
      oreResource -= 3;
      score++;
      updateResourcePanels(-5);
      updateScorePanel();

      Message msg = new Message(MessageType.UPGRADE_SETTLEMENT, "Now", clientUsername, circleId);
      msg.setUserColor(this.userColor);
      stompClient.sendCommand(msg);
      checkScore();
    } catch (Exception e) {
      e.printStackTrace();
    }
    playSoundEffect(buildSound);
  }

  /**
   * A method to upgrade settlement to a city and visually represent it on the board for all
   * players.
   *
   * @param msg - Message instance which contains the upgraded settlement's information.
   */
  public void settlementUpgraded(Message msg) {
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (node.getId().equals(msg.getContent())) {
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          node.setStyle(String.format("-fx-fill: %s;", msg.getUserColor()));
          node.setScaleX(1.3);
          node.setScaleY(1.3);
          break;
        }
      }
    }
    occupiedEdges.add(msg.getContent());
  }

  /** A method to open trade panel in the bottom-left corner of the screen. */
  @FXML
  public void openTradePanel() {
    tradePanel.setVisible(true);
  }

  /** A method to close trade offer panel in the bottom-left corner of the screen. */
  @FXML
  public void closeTradePanels() {
    tradePanel.setVisible(false);
  }

  /** A method to close trade offer panel in the top-right corner of the screen. */
  @FXML
  public void closeOfferPanel() {
    tradeOfferPanel.setVisible(false);
    tradeAcceptButton.setVisible(false);
    tradeRejectButton.setVisible(false);
  }

  /**
   * A method to send trade offer through websocket connection.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void sendTradeOffer() throws JsonProcessingException {
    RadioButton resourceGiveButton = (RadioButton) resourceGiveGroup.getSelectedToggle();
    RadioButton resourceGetButton = (RadioButton) resourceGetGroup.getSelectedToggle();
    closeTradePanels();
    playSoundEffect(buttonSound);

    if (resourceGiveButton == null || resourceGetButton == null) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Please select resource to crate a trade!");
    } else {
      String resourceTypes =
          getResourceTypes(
              resourceGiveButton.getStyleClass().toString(),
              resourceGetButton.getStyleClass().toString());
      if (checkResourcesForTrade(
          resourceTypes.split("/")[0],
          this.brickResource,
          this.lumberResource,
          this.oreResource,
          this.grainResource,
          this.woolResource)) {
        Message msg =
            new Message(MessageType.TRADE_OFFER_SENT, "Now", this.clientUsername, resourceTypes);
        stompClient.sendCommand(msg);
        tradeButton.setDisable(true);
      } else {
        NotificationHelper.showAlert(
            Alert.AlertType.ERROR, "Error", "You don't have enough resource for this trade!");
      }
    }
  }

  /**
   * A method to show trade offer panel after getting TRADE_OFFER_RECEIVED message through websocket
   * connection.
   *
   * @param msg - Message instance came from websocket connection.
   */
  public void showTradeOffer(Message msg) {
    tradeUsername.setText(msg.getNickname());
    String[] resources = msg.getContent().split("/");
    tradeGivenResource.getStyleClass().add(String.format("%sTrade", resources[0]));
    tradeWantedResource.getStyleClass().add(String.format("%sTrade", resources[1]));

    tradeOfferPanel.setVisible(true);
    if (!msg.getNickname().equals(this.clientUsername)) {
      tradeAcceptButton.setVisible(
          checkResourcesForTrade(
              resources[1],
              brickResource,
              lumberResource,
              oreResource,
              grainResource,
              woolResource));
      tradeRejectButton.setVisible(true);
    }
  }

  /**
   * A method to call after accepting a trade. Sends a message back to websocket with message type
   * TRADE_OFFER_ACCEPTED.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void acceptTradeOffer() throws JsonProcessingException {
    String resourceTypes =
        getResourceTypes(
            tradeGivenResource.getStyleClass().toString(),
            tradeWantedResource.getStyleClass().toString());
    Message msg =
        new Message(
            MessageType.TRADE_OFFER_ACCEPTED, "Now", tradeUsername.getText(), resourceTypes);
    stompClient.sendCommand(msg);
    String[] resources = resourceTypes.split("/");
    updateResourcesAfterTrade(resources[0], resources[1]);
    refreshClientResources();
    closeOfferPanel();
  }

  /**
   * A method called after trade offer has been accepted. Refreshes client resource panels.
   *
   * @param msg - Message instance which contains resource type that are used in trade.
   */
  public void tradeOfferAccepted(Message msg) {
    closeOfferPanel();
    String[] resources = msg.getContent().split("/");
    if (msg.getNickname().equals(this.clientUsername)) {
      updateResourcesAfterTrade(resources[1], resources[0]);
      refreshClientResources();
    }
    removeResourceTradeType(tradeGivenResource, tradeWantedResource);
  }

  /**
   * A method to update resources if the trade offer is accepted.
   *
   * @param gainedResource - Gained resource type by the trade.
   * @param lostResource - Lost resource type by the trade.
   */
  public void updateResourcesAfterTrade(String gainedResource, String lostResource) {
    switch (gainedResource) {
      case "brick" -> brickResource++;
      case "lumber" -> lumberResource++;
      case "ore" -> oreResource++;
      case "grain" -> grainResource++;
      case "wool" -> woolResource++;
    }

    switch (lostResource) {
      case "brick" -> brickResource--;
      case "lumber" -> lumberResource--;
      case "ore" -> oreResource--;
      case "grain" -> grainResource--;
      case "wool" -> woolResource--;
    }
  }

  /**
   * A method to check the score and decide if the game is ended by the current player. If score is
   * high enough GAME_ENDED message is sent through the websocket connection.
   *
   * @throws IOException - This is an exception for creating the request.
   * @throws InterruptedException - This is an exception for sending the request.
   */
  public void checkScore() throws IOException, InterruptedException {
    if ((this.score + (this.haveLongestRoad && this.longestRoad >= 5 ? 2 : 0)) >= 8) {
      Message msg =
          new Message(MessageType.GAME_ENDED, "Now", this.clientUsername, this.clientUsername);
      stompClient.sendCommand(msg);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(
                  URI.create(
                      String.format(
                          "http://localhost:8080/api/game/closeGame?gameId=%s", this.gameId)))
              .header("Content-Type", "application/json")
              .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
              .PUT(HttpRequest.BodyPublishers.ofString(""))
              .build();

      HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
  }

  /**
   * A method to call when the game has been finished. According to results, different actions are
   * taken. Winners and losers see and hear different result banners and sounds. GameHistory is
   * logged at the end of the game for each player.
   *
   * @param msg - Message instance.
   */
  public void gameEnded(Message msg) {
    if (gameEndedAlready) {
      return;
    }
    gameEndedAlready = true;
    addChatMessage(msg.getContent() + " won the game!");
    toggleOffButtons(true);
    resultBanner.setVisible(true);
    backButton.setVisible(true);
    backgroundPlayer2.stop();

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> body = new HashMap<>();
    body.put("gameId", this.gameId);
    body.put("username", this.clientUsername);
    body.put(
        "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    body.put("didWon", msg.getContent().equals(this.clientUsername) ? 1 : 0);
    body.put("totalScore", (score + (this.haveLongestRoad && this.longestRoad >= 5 ? 2 : 0)));

    if (msg.getContent().equals(this.clientUsername)) {
      playSoundEffect(victoriousSound);
      resultBanner.setText("You are victorious!");
      addChatMessage(this.clientUsername + " has won the game!");
    } else {
      playSoundEffect(defeatedSound);
      resultBanner.setText("You have been defeated!");
    }
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8080/api/game/game-history"))
              .header("Content-Type", "application/json")
              .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
              .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
              .build();
      HttpResponse<String> response =
          HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * A method to allow user to get back to the lobby screen at the end of the game.
   *
   * @throws IOException - This is an exception for the scene load function.
   */
  @FXML
  public void backToMenu() throws IOException {
    playSoundEffect(buttonSound);
    switchBackgroundMusic(false);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/lobbyView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * A method to highlight player info box at the right panel.
   *
   * @param username - Username of the players which will be highlighted.
   */
  public void highlightPlayerInfoBox(String username) {
    for (Node node : playerInfoBox.getChildren()) {
      if (node.getClass().getName().contains("VBox")) {
        if (node.getId().equals(username)) {
          node.setStyle(
              "-fx-background-color: D3D3D3; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0); -fx-background-color: #bc9d7e;");
        } else {
          node.setStyle("-fx-background-color: transparent;");
        }
      }
    }
  }

  /**
   * A method to send chat messages through websocket connection.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void sendChatMessage() throws JsonProcessingException {
    Message msg =
        new Message(MessageType.IN_GAME_CHAT, "Now", clientUsername, chatTextField.getText());
    msg.setUserColor(this.userColor);
    chatTextField.setText("");
    stompClient.sendChatMessage(msg);
  }

  /**
   * A method to add a chat message with a color.
   *
   * @param msg - Message instance.
   */
  public void addChatMessage(Message msg) {
    Text txt1 = new Text(msg.getNickname() + ": ");
    Text txt2 = new Text(msg.getContent());

    Color clr =
        switch (msg.getUserColor()) {
          case "red" -> Color.RED;
          case "orange" -> Color.ORANGE;
          case "green" -> Color.GREEN;
          case "pink" -> Color.PINK;
          default -> Color.BLACK;
        };
    txt1.setFill(clr);

    TextFlow textFlow = new TextFlow(txt1, txt2);
    chatBox.getChildren().add(textFlow);
    chatScrollPane.setVvalue(1D);
  }

  /**
   * A method to add a generic chat message.
   *
   * @param message - Message instance.
   */
  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Text(message));
    chatScrollPane.setVvalue(1D);
  }
}
