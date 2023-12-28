package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group12.helper.StompClient;
import com.group12.model.GameData;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.HttpClientHelper.getSessionCookie;

@Component
public class RoomController {

  private StompClient stompClient;

  @FXML private Button sendMessageButton;

  @FXML private TextField chatTextField;

  @FXML private BorderPane borderpn;

  @FXML private VBox chatBox;

  @FXML private Label gameName;

  public void initialize() throws JsonProcessingException, URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);

    stompClient = new StompClient(this);
    stompClient.connect();
  }

  public void initData(GameData gameData) {
    gameName.setText(gameData.getGameName());
  }

  public void addChatMessage(String message) {
    chatBox.getChildren().add(new Label(message));
  }

  @FXML
  public void sendChatMessage() throws JsonProcessingException {
    Message msg =
        new Message(MessageType.CHAT, "Now", getSessionCookie("username"), chatTextField.getText());
    chatTextField.setText("");
    stompClient.sendChatMessage(msg);
  }
}
