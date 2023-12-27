package com.group12.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.helper.NotificationHelper;
import com.group12.model.GameData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

@Component
public class GameCreationController {

  @FXML private Button backButton;

  @FXML private Button createButton;

  @FXML private ToggleGroup cpuOrOnline;

  @FXML private ToggleGroup passwordRequired;

  @FXML private TextField lobbyNameText;

  @FXML private TextField lobbyDescriptionText;

  @FXML private Label passwordLabel;

  @FXML private TextField passwordText;

  @FXML private BorderPane borderpn;

  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);
  }

  @FXML
  public void createGame() throws IOException, InterruptedException {
    playSoundEffect(buttonSound);
    RadioButton cpuOrOnlineButton = (RadioButton) cpuOrOnline.getSelectedToggle();
    RadioButton passwordRequiredButton = (RadioButton) passwordRequired.getSelectedToggle();

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> body = new HashMap<>();
    body.put("gameName", lobbyNameText.getText());
    body.put("gameDescription", lobbyDescriptionText.getText());
    body.put("passwordRequired", passwordRequiredButton.getText().equals("Yes"));
    body.put(
        "gamePassword",
        passwordRequiredButton.getText().equals("Yes") ? passwordText.getText() : null);
    body.put("gameLeader", Integer.parseInt(HttpClientHelper.getSessionCookie("userId")));

    if (cpuOrOnlineButton.getText().equals("CPU")) {
      // CPU
      body.put("online", false);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8080/api/game"))
              .header("Content-Type", "application/json")
              .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
              .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
              .build();

      HttpResponse<String> response =
          HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        NotificationHelper.showAlert(
            Alert.AlertType.INFORMATION, "Success", "New lobby has been created!");
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gameView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();

      } else {
        NotificationHelper.showAlert(
            Alert.AlertType.ERROR, "Error", "There was an error! Please try again.");
      }
    } else {
      // Online
      body.put("online", true);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8080/api/game"))
              .header("Content-Type", "application/json")
              .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
              .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
              .build();

      HttpResponse<String> response =
          HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        GameData gData = objectMapper.readValue(response.body(), GameData.class);

        NotificationHelper.showAlert(
            Alert.AlertType.INFORMATION, "Success", "New lobby has been created!");
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/roomView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        RoomController roomController = fxmlLoader.getController();
        roomController.initData(gData);
        stage.show();
      } else {
        NotificationHelper.showAlert(
            Alert.AlertType.ERROR, "Error", "There was an error! Please try again.");
      }
    }
  }

  @FXML
  public void passwordRequiredAction(ActionEvent event) {
    final RadioButton source = (RadioButton) event.getSource();
    passwordText.disableProperty().set(!source.getText().equals("Yes"));
    passwordLabel.disableProperty().set(!source.getText().equals("Yes"));
  }

  @FXML
  public void backToLobby() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/lobbyView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
