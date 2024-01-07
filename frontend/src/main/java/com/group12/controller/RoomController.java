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
  private Boolean isGameCreated;
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

  /**
   * A controller method to initialize some data about the game at the very beginning.
   *
   * @param gameData - Game data that is fetched from the database.
   * @param gameId - Game id.
   * @param isGameCreated - Boolean variable to know if the game is created by the client user.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  public void initData(GameData gameData, String gameId, Boolean isGameCreated)
      throws JsonProcessingException {
    this.gameLeader = gameData.getGameLeader();
    gameName.setText(gameData.getGameName());
    startGameButton.setVisible(getSessionCookie("username").equals(gameLeader));
    this.gameId = gameId;
    this.isGameCreated = isGameCreated;
    addNewSessionCookie("gameId", gameId);
    stompClient = new StompClient(this, getSessionCookie("username"), this.gameId);
    stompClient.connect();
  }

  /**
   * A method to set and initialize some data (button actions, background image, etc.)
   *
   * @throws URISyntaxException - Throws exception.
   */
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

  /** A method to send GAME_CREATED or USER_JOINED messages according to joining room. */
  public void sendAfterConnection() {
    try {
      if (this.isGameCreated) {
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
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  /**
   * A method to send JOIN message over websocket connection after the game is created by the
   * leader.
   */
  public void gameCreated() {
    try {
      stompClient.sendCommand(
          new Message(
              MessageType.USER_JOINED, "NOW", getSessionCookie("username"), "New user joined!"));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  /**
   * A message with colored text to add into the chat box.
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
          case "blue" -> Color.BLUE;
          case "purple" -> Color.PURPLE;
          default -> Color.BLACK;
        };
    txt1.setFill(clr);

    TextFlow textFlow = new TextFlow(txt1, txt2);
    chatBox.getChildren().add(textFlow);
    chatScrollPane.setVvalue(1D);
  }

  /**
   * A generic message with black text to add into the chat box.
   *
   * @param message - Message instance.
   */
  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Text(message));
    chatScrollPane.setVvalue(1D);
  }

  /**
   * A method to refresh user list of the room whenever a player is joined or left.
   *
   * @param msg - Message instance.
   */
  public void refreshPlayerList(Message msg) {
    playerUsernameList = msg.getContent().split("/");
    userColorList = msg.getUserColorList().split("/");
    userReadyList = msg.getUserReadyList().split("/");

    this.playerList.getChildren().clear();
    for (int i = 0; i < playerUsernameList.length; i++) {
      addPlayerToTheList(playerUsernameList[i], userColorList[i], userReadyList);
    }
    startGameButton.setDisable(
        userReadyList.length < 2
            || userReadyList.length > 4
            || userReadyList.length != playerUsernameList.length);
  }

  /**
   * A method to add a new player to the room's user list.
   *
   * @param username - Username that will be added.
   * @param color - User color that will be added.
   * @param readyList - Users ready status as a list.
   */
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

  /**
   * A button action to send a message to the room topic via websocket connection to kick a player.
   *
   * @param event - Mouse event
   */
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

  /**
   * A method to kick user for all clients.
   *
   * @param msg - Message instance.
   */
  public void userKicked(Message msg) {
    try {
      if (msg.getContent().equals(getSessionCookie("username"))) {
        leaveRoom();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * A button action to send a message to the room topic via websocket connection to set player
   * status as READY.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void readyAction() throws JsonProcessingException {
    playSoundEffect(buttonSound);
    Message msg =
        new Message(MessageType.READY, "Now", getSessionCookie("username"), "User is ready!");
    stompClient.sendCommand(msg);
  }

  /**
   * A method to switch color of ready status box from red to green.
   *
   * @param username - Provided username with the read status is achieved with the last ws message.
   */
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

  /**
   * A method to toggle on/off the start game button.
   *
   * @param isFull - Boolean variable to parametrize if the lobby is full or not.
   */
  @FXML
  public void toggleStartGameButton(Boolean isFull) {
    startGameButton.setDisable(!isFull);
  }

  /**
   * A button action to send a message to the room topic via websocket connection to let everyone in
   * the lobby know the game is started.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  @FXML
  public void startGame() throws JsonProcessingException {
    playSoundEffect(buttonSound);
    Message msg =
        new Message(
            MessageType.START_GAME, "Now", getSessionCookie("username"), "The game is starting!");
    stompClient.sendCommand(msg);
  }

  /**
   * A method to load game screen after game isi started by the leader.
   *
   * @throws IOException - throws an exception while sending a message.
   */
  public void showGameScene() throws IOException {
    NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "The game has started!");
    Stage stage = (Stage) startGameButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/onlineGameView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    OnlineGameController gameController = fxmlLoader.getController();
    stompClient.setGameController(gameController);
    gameController.initData(
        this.stompClient, this.userColor, this.playerUsernameList, this.userColorList, this.gameId);
    stage.show();
  }

  /**
   * A button action to send a message to the chat topic via websocket connection.
   *
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
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

  /**
   * A button action to leave the room. Sends a message to the websocket with LEAVE message type to
   * let everyone knowing.
   *
   * @throws IOException - throws an exception while sending a message.
   */
  @FXML
  public void leaveRoom() throws IOException {
    Message msg =
        new Message(MessageType.LEAVE, "Now", getSessionCookie("username"), "Yo I'm leaving!");
    msg.setUserColor(this.userColor);
    stompClient.sendCommand(msg);
  }

  /**
   * A method to leave a room by disconnecting from the websocket.
   *
   * @param msg - Message instance
   */
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
