package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group12.helper.StompClient;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.group12.controller.GameController.d1;
import static com.group12.controller.GameController.d2;
import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.MediaHelper.diceEffect;
import static com.group12.helper.MediaHelper.playSoundEffect;

@Component
public class OnlineGameController {
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

  @FXML private Button skipTurnButton;

  @FXML private ImageView firstDiceImage;
  @FXML private ImageView secondDiceImage;

  @FXML private Button roadBuildButton;
  @FXML private Button settlementBuildButton;
  @FXML private Button settlementUpgradeButton;

  @FXML private Label resultBanner;
  @FXML private Button backButton;

  private StompClient stompClient;
  private String clientUsername;

  @FXML private TextField chatTextField;
  @FXML private Button sendButton;
  @FXML private ScrollPane chatScrollPane;
  @FXML private VBox chatBox;

  public void initData(StompClient stompClient) {
    this.stompClient = stompClient;
  }

  public void initialize() {
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
  }

  @FXML
  public void showOptionalRoads() {}

  @FXML
  public void showOptionalSettlements() {}

  @FXML
  public void showOptionalUpgrades() {}

  @FXML
  public void skipTurn() {}

  @FXML
  public void throwDice(MouseEvent event) throws JsonProcessingException {
    Message msg =
        new Message(MessageType.THROW_DICE, "Now", clientUsername, "Dice has been thrown!");
    stompClient.sendCommand(msg);
  }

  public void diceThrowAnimation(int firstDiceResult, int secondDiceResult) {
    rollDice(firstDiceImage, firstDiceResult);
    rollDice(secondDiceImage, secondDiceResult);
    playSoundEffect(diceEffect);
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
}
