package com.group12.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group12.helper.StompClient;
import com.group12.model.GameData;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.HttpClientHelper.getSessionCookie;

@Component
public class RoomController {

  private StompClient stompClient;

  @FXML private BorderPane borderpn;

  @FXML private Label gameName;

  public void initialize() throws JsonProcessingException, URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);

    stompClient = new StompClient();
    stompClient.connect();
    stompClient.send(
        new Message(MessageType.CONNECT, "NOW", getSessionCookie("username"), "hello man"));
  }

  public void initData(GameData gameData) {
    gameName.setText(gameData.getGameName());
  }
}
