package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group12.helper.NotificationHelper;
import com.group12.helper.StompClient;
import com.group12.model.GameData;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.HttpClientHelper.addNewSessionCookie;
import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

@Component
public class RoomController {

  private StompClient stompClient;
  private String userColor;
  private String gameLeader;
  private String gameId;
  private String[] playerUsernameList;
  private String[] userColorList;
  private String[] userReadyList;

  @FXML private Button sendMessageButton;
  @FXML private TextField chatTextField;
  @FXML private BorderPane borderpn;
  @FXML private ScrollPane chatScrollPane;
  @FXML private VBox chatBox;
  @FXML private VBox playerList;
  @FXML private Label gameName;
  @FXML private Button readyButton;
  @FXML private Button startGameButton;
  @FXML private Button backButton;

  public void initData(GameData gameData, String gameId, Boolean isGameCreated)
      throws JsonProcessingException {
    this.gameLeader = gameData.getGameLeader();
    gameName.setText(gameData.getGameName());
    startGameButton.setVisible(getSessionCookie("username").equals(gameLeader));
    this.gameId = gameId;
    addNewSessionCookie("gameId", gameId);
    stompClient = new StompClient(this, getSessionCookie("username"), this.gameId);
    stompClient.connect();
    if (isGameCreated) {
      stompClient.sendCommand(
          new Message(
              MessageType.GAME_CREATED,
              "NOW",
              getSessionCookie("username"),
              "A new game has been created!"));
    } else {
      stompClient.sendCommand(
          new Message(
              MessageType.USER_JOINED, "NOW", getSessionCookie("username"), "New user joined!"));
    }
  }

  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);
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

  public void gameCreated() {
    try {
      stompClient.sendCommand(
          new Message(
              MessageType.USER_JOINED, "NOW", getSessionCookie("username"), "New user joined!"));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
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
          case "blue" -> Color.BLUE;
          case "purple" -> Color.PURPLE;
          default -> Color.BLACK;
        };
    txt1.setFill(clr);

    TextFlow textFlow = new TextFlow(txt1, txt2);
    chatBox.getChildren().add(textFlow);
    chatScrollPane.setVvalue(1D);
  }

  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Text(message));
    chatScrollPane.setVvalue(1D);
  }

  public void refreshPlayerList(Message msg) {
    playerUsernameList = msg.getContent().split("/");
    userColorList = msg.getUserColorList().split("/");
    userReadyList = msg.getUserReadyList().split("/");

    this.playerList.getChildren().clear();
    for (int i = 0; i < playerUsernameList.length; i++) {
      addPlayerToTheList(playerUsernameList[i], userColorList[i], userReadyList);
    }
    startGameButton.setDisable(userReadyList.length != 2);
  }

  public void addPlayerToTheList(String username, String color, String[] readyList) {
    if (username.equals(getSessionCookie("username"))) {
      this.userColor = color;
    }

    HBox hBox = new HBox(10);
    hBox.setId(username);
    hBox.setAlignment(Pos.CENTER_LEFT);
    hBox.setPrefHeight(80);
    hBox.setPrefWidth(300);
    hBox.setBackground(
        new Background(
            new BackgroundFill(Color.rgb(188, 157, 126), CornerRadii.EMPTY, Insets.EMPTY)));
    hBox.setBorder(
        new Border(
            new BorderStroke(
                Color.rgb(47, 14, 6),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT)));
    hBox.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 5, 0, 0, 0);");

    VBox readyBox = new VBox();
    readyBox.setPrefHeight(50);
    readyBox.setPrefWidth(50);
    readyBox.setStyle(
        String.format(
            "-fx-background-color: %s;",
            (Arrays.asList(readyList).contains(username) ? "green" : "red")));

    Label label = new Label(username);
    label.setStyle("-fx-font-weight: bold");

    hBox.getChildren().add(readyBox);
    hBox.getChildren().add(label);

    if (this.gameLeader.equals(getSessionCookie("username")) && !username.equals(this.gameLeader)) {
      Button kickButton = new Button("Kick");
      kickButton.setStyle(
          "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 5, 0, 0, 0);-fx-cursor: hand;-fx-background-color: #2f0e06;-fx-text-fill: white;");
      kickButton.setOnAction(this::kickUser);
      hBox.getChildren().add(kickButton);
    }

    playerList.getChildren().add(hBox);
  }

  @FXML
  public void kickUser(ActionEvent event) {
    playSoundEffect(buttonSound);
    Button kickButton = (Button) event.getSource();
    String kickedUsername = kickButton.getParent().getId();
    try {
      Message msg =
          new Message(MessageType.KICK, "Now", getSessionCookie("username"), kickedUsername);
      stompClient.sendCommand(msg);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void userKicked(Message msg) {
    try {
      if (msg.getContent().equals(getSessionCookie("username"))) {
        leaveRoom();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void readyAction() throws JsonProcessingException {
    playSoundEffect(buttonSound);
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
    playSoundEffect(buttonSound);
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
    gameController.initData(this.stompClient, this.userColor, this.playerUsernameList, this.userColorList, this.gameId);
    stage.show();
  }

  @FXML
  public void sendChatMessage() throws JsonProcessingException {
    playSoundEffect(buttonSound);
    Message msg =
        new Message(
            MessageType.LOBBY_CHAT, "Now", getSessionCookie("username"), chatTextField.getText());
    msg.setUserColor(this.userColor);

    chatTextField.setText("");
    stompClient.sendChatMessage(msg);
  }

  @FXML
  public void leaveRoom() throws IOException {
    Message msg =
        new Message(MessageType.LEAVE, "Now", getSessionCookie("username"), "Yo I'm leaving!");
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);
  }

  public void leftRoom(Message msg) {
    if (msg.getNickname().equals(getSessionCookie("username"))) {
      stompClient.disconnect();

      try {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/lobbyView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
