package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group12.helper.NotificationHelper;
import com.group12.helper.StompClient;
import com.group12.model.GameData;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.HttpClientHelper.getSessionCookie;

@Component
public class RoomController {

  private StompClient stompClient;
  private String gameLeader;

  @FXML private Button sendMessageButton;
  @FXML private TextField chatTextField;
  @FXML private BorderPane borderpn;
  @FXML private VBox chatBox;
  @FXML private VBox playerList;
  @FXML private Label gameName;
  @FXML private Button readyButton;
  @FXML private Button startGameButton;

  public void initData(GameData gameData) {
    this.gameLeader = gameData.getGameLeader();
    gameName.setText(gameData.getGameName());
    startGameButton.setVisible(getSessionCookie("username").equals(gameLeader));
  }

  public void initialize() throws JsonProcessingException, URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);

    stompClient = new StompClient(this);
    stompClient.connect();
    stompClient.sendCommand(
        new Message(
            MessageType.USER_JOINED, "NOW", getSessionCookie("username"), "New user joined!"));

    stompClient.sendCommand(
        new Message(
            MessageType.USER_LIST, "NOW", getSessionCookie("username"), "Get player list!"));

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
  }

  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Label(message));
  }

  public void refreshPlayerList(String usernameList) {
    String[] playerUsernameList = usernameList.split("/");
    if (this.playerList.getChildren().size() == playerUsernameList.length - 1) {
      addPlayerToTheList(playerUsernameList[playerUsernameList.length - 1]);
    } else {
      for (String username : playerUsernameList) {
        addPlayerToTheList(username);
      }
    }
  }

  public void addPlayerToTheList(String username) {
    HBox hBox = new HBox();
    hBox.setId(username);
    hBox.setAlignment(Pos.CENTER_LEFT);
    hBox.setPrefHeight(80);
    hBox.setPrefWidth(300);
    hBox.setBorder(
        new Border(
            new BorderStroke(
                Color.valueOf("#000000"),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT)));

    VBox readyBox = new VBox();
    readyBox.setPrefHeight(50);
    readyBox.setPrefWidth(50);
    readyBox.setStyle("-fx-background-color: red;");

    Label label = new Label(username);

    hBox.getChildren().add(readyBox);
    hBox.getChildren().add(label);

    playerList.getChildren().add(hBox);
  }

  @FXML
  public void readyAction() throws JsonProcessingException {
    Message msg =
        new Message(MessageType.READY, "Now", getSessionCookie("username"), "User is ready!");
    stompClient.sendCommand(msg);
  }

  @FXML
  public void setReadyColor(String username) {
    if (username.equals(getSessionCookie("username"))) {
      readyButton.setDisable(true);
    }
    for (Node hbox : playerList.getChildren()) {
      if (hbox.getId().equals(username)) {
        HBox hbox1 = (HBox) hbox;
        VBox vbox = (VBox) hbox1.getChildren().get(0);
        vbox.setStyle("-fx-background-color: green;");
        break;
      }
    }
  }

  @FXML
  public void toggleStartGameButton(Boolean isFull) {
    startGameButton.setDisable(!isFull);
  }

  @FXML
  public void startGame() throws JsonProcessingException {
    Message msg =
        new Message(
            MessageType.START_GAME, "Now", getSessionCookie("username"), "The game is starting!");
    stompClient.sendCommand(msg);
  }

  public void showGameScene() throws IOException {
    NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "The game has started!");
    Stage stage = (Stage) startGameButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/onlineGameView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    OnlineGameController gameController = fxmlLoader.getController();
    stompClient.setGameController(gameController);
    gameController.initData(stompClient);
    stage.show();
  }

  @FXML
  public void sendChatMessage() throws JsonProcessingException {
    Message msg =
        new Message(MessageType.LOBBY_CHAT, "Now", getSessionCookie("username"), chatTextField.getText());
    chatTextField.setText("");
    stompClient.sendChatMessage(msg);
  }
}
