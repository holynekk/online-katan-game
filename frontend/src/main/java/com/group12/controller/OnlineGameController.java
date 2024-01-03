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
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.group12.helper.GameHelper.circleNeighbours;
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
      //      hbox.setStyle(
      //          String.format("-fx-border-color: %s; -fx-border-width: 2 0 2 0;",
      // this.userColor));
      Label label1 = new Label("r: ");
      label1.setTextFill(Color.WHITE);
      hbox.getChildren().add(label1);

      Label label2 = new Label("0");
      label2.setTextFill(Color.WHITE);
      label2.setId(username + "Resource");
      hbox.getChildren().add(label2);

      Label label3 = new Label("l: ");
      label3.setTextFill(Color.WHITE);
      hbox.getChildren().add(label3);

      Label label4 = new Label("0");
      label4.setTextFill(Color.WHITE);
      label4.setId(username + "LongestRoad");
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
    playerInfoBox.getChildren().get(1).setStyle("-fx-fill: #808080;");
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
        node.setStyle("-fx-fill: #808080;");
        node.setOnMouseClicked(this::upgradeSettlement);
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
    updateResourcePanels(totalGain);
  }

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

  public void updateResourcePanels(int totalGain) throws JsonProcessingException {
    refreshClientResources();
    Message msg =
        new Message(
            MessageType.RESOURCE_CHANGE,
            "Now",
            this.clientUsername,
            totalGain + "/" + longestRoad + "/" + score);
    stompClient.sendCommand(msg);
  }

  public void refreshClientResources() {
    brickText.setText(Integer.toString(brickResource));
    oreText.setText(Integer.toString(oreResource));
    lumberText.setText(Integer.toString(lumberResource));
    grainText.setText(Integer.toString(grainResource));
    woolText.setText(Integer.toString(woolResource));
  }

  public void updateScorePanel() throws JsonProcessingException {
    Message msg =
        new Message(
            MessageType.RESOURCE_CHANGE,
            "Now",
            this.clientUsername,
            "0/" + longestRoad + "/" + score);
    stompClient.sendCommand(msg);
  }

  public void setPlayerResourceInfoPanel(String username, String msgContent) {
    String[] playerInfo = msgContent.split("/");

    Label text = (Label) anchPane.getScene().lookup("#" + username + "Resource");
    text.setText(
        Integer.toString(Integer.parseInt(text.getText()) + Integer.parseInt(playerInfo[0])));
  }

  public void setPlayerScorePanel(String username, String msgContent) {
    String[] playerInfo = msgContent.split("/");

    Label text = (Label) anchPane.getScene().lookup("#" + username + "LongestRoad");
    text.setText(Integer.toString(Integer.parseInt(playerInfo[1])));

    text = (Label) anchPane.getScene().lookup("#" + username + "Score");
    text.setText(Integer.toString(Integer.parseInt(playerInfo[2])));
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
    closeTradePanels();
    tradeOfferPanel.setVisible(false);
    tradeAcceptButton.setVisible(false);
    tradeRejectButton.setVisible(false);
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
    if (grainResource >= 2 && oreResource >= 3 && ownedCities.size() < ownedCircles.size()) {
      settlementUpgradeButton.setDisable(false);
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

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);

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

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);

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
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);
  }

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
  public void upgradeSettlement(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();

    ownedCities.add(circleId);

    roadBuildButton.setDisable(true);
    settlementBuildButton.setDisable(true);
    settlementUpgradeButton.setDisable(true);

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
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

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

  @FXML
  public void openTradePanel() {
    tradePanel.setVisible(true);
  }

  @FXML
  public void closeTradePanels() {
    tradePanel.setVisible(false);
  }

  @FXML
  public void sendTradeOffer() throws JsonProcessingException {
    RadioButton resourceGiveButton = (RadioButton) resourceGiveGroup.getSelectedToggle();
    RadioButton resourceGetButton = (RadioButton) resourceGetGroup.getSelectedToggle();
    closeTradePanels();

    if (resourceGiveButton == null || resourceGetButton == null) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Please select resource to crate a trade!");
    } else {
      String resourceTypes =
          getResourceTypes(resourceGiveButton.getStyleClass(), resourceGetButton.getStyleClass());
      Message msg =
          new Message(MessageType.TRADE_OFFER_SENT, "Now", this.clientUsername, resourceTypes);
      stompClient.sendCommand(msg);
    }
  }

  public void showTradeOffer(Message msg) {
    tradeUsername.setText(msg.getNickname());
    String[] resources = msg.getContent().split("/");
    tradeGivenResource.getStyleClass().add(String.format("%sTrade", resources[0]));
    tradeWantedResource.getStyleClass().add(String.format("%sTrade", resources[1]));

    tradeOfferPanel.setVisible(true);
    if (!msg.getNickname().equals(this.clientUsername)) {
      tradeAcceptButton.setVisible(true);
      tradeRejectButton.setVisible(true);
    }
  }

  @FXML
  public void acceptTradeOffer() throws JsonProcessingException {
    String resourceTypes =
        getResourceTypes(tradeGivenResource.getStyleClass(), tradeWantedResource.getStyleClass());
    Message msg =
        new Message(
            MessageType.TRADE_OFFER_ACCEPTED, "Now", tradeUsername.getText(), resourceTypes);
    stompClient.sendCommand(msg);
    brickResource++;
    lumberResource--;
    refreshClientResources();
    tradeOfferPanel.setVisible(false);
    tradeAcceptButton.setVisible(false);
    tradeRejectButton.setVisible(false);
  }

  public void tradeOfferAccepted(Message msg) {
    closeTradePanels();
    String[] resources = msg.getContent().split("/");
    if (msg.getNickname().equals(this.clientUsername)) {
      brickResource--;
      lumberResource++;
      refreshClientResources();
    }
  }

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
    msg.setUserColor(this.userColor);
    chatTextField.setText("");
    stompClient.sendChatMessage(msg);
  }

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

  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Label(message));
    chatScrollPane.setVvalue(1D);
  }

  public String getClientUsername() {
    return this.clientUsername;
  }
}
