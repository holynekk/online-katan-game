package com.group12.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.model.GameData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class LobbyController {

  @FXML private Button backButton;

  @FXML private Button newLobbyButton;

  @FXML private Label lobbyNameLabel;

  @FXML private Label lobbyIdLabel;

  @FXML private Label lobbyPasswordRequiredLabel;

  @FXML private Label lobbyGameLeaderLabel;

  @FXML private ListView<GameData> lobbyListView;

  public void initialize() throws IOException, InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/game/list"))
            .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
            .build();
    HttpResponse<String> response =
        HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      List<GameData> gameList =
          objectMapper.readValue(response.body(), new TypeReference<List<GameData>>() {});

      lobbyListView.getItems().setAll(gameList);
      lobbyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      lobbyListView.getSelectionModel().selectFirst();

      lobbyIdLabel.setText(Integer.toString(gameList.get(0).getGameId()));
      lobbyNameLabel.setText(gameList.get(0).getGameName());
      lobbyPasswordRequiredLabel.setText(gameList.get(0).getPasswordRequired() ? "Yes" : "No");
      lobbyGameLeaderLabel.setText(gameList.get(0).getGameLeader());
    }
  }

  @FXML
  public void handleClickListView() {
    GameData gameData = lobbyListView.getSelectionModel().getSelectedItem();
    lobbyIdLabel.setText(Integer.toString(gameData.getGameId()));
    lobbyNameLabel.setText(gameData.getGameName());
    lobbyPasswordRequiredLabel.setText(gameData.getPasswordRequired() ? "Yes" : "No");
    lobbyGameLeaderLabel.setText(gameData.getGameLeader());
  }

  @FXML
  public void showLeaderboard() throws IOException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/leaderboardView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void showGameCreation() throws IOException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gameCreationView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void backToMenu() throws IOException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
