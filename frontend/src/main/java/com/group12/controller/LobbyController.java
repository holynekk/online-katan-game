package com.group12.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.helper.NotificationHelper;
import com.group12.model.GameData;
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
import java.util.List;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

/**
 * The {@code LobbyController} class handles the user interface and interactions within the game
 * lobby. This controller manages the display of available game rooms, allows users to create new
 * lobbies, join existing ones, and navigate back to the main menu.
 *
 * <p>The class communicates with a REST API to fetch and display the list of game lobbies and
 * handles the lobby joining process.
 */
@Component
public class LobbyController {

  @FXML private Button backButton;

  @FXML private Button newLobbyButton;

  @FXML private Label lobbyNameLabel;

  @FXML private Label lobbyIdLabel;

  @FXML private Label lobbyPasswordRequiredLabel;

  @FXML private Label lobbyGameLeaderLabel;

  @FXML private ListView<GameData> lobbyListView;

  @FXML private Button joinRoom;

  @FXML private BorderPane borderpn;

  /**
   * An initialize method to populate tables, user information, background image, etc.
   *
   * @throws IOException - Throws an exception when there is a problem with loading * * background
   *     images.
   * @throws InterruptedException - Throws an exception when there is a problem with loading * *
   *     background images.
   * @throws URISyntaxException - Throws an exception when there is a problem with loading * *
   *     background images.
   */
  public void initialize() throws IOException, InterruptedException, URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);

    ObjectMapper objectMapper = new ObjectMapper();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("https://group12-katan-backend.onrender.com/api/game/list"))
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

  /**
   * A button action to set the information at the center after clicking to a listed lobby name at
   * the left panel.
   */
  @FXML
  public void handleClickListView() {
    playSoundEffect(buttonSound);
    GameData gameData = lobbyListView.getSelectionModel().getSelectedItem();
    lobbyIdLabel.setText(Integer.toString(gameData.getGameId()));
    lobbyNameLabel.setText(gameData.getGameName());
    lobbyPasswordRequiredLabel.setText(gameData.getPasswordRequired() ? "Yes" : "No");
    lobbyGameLeaderLabel.setText(gameData.getGameLeader());
  }

  /**
   * A button action to show leaderboard screen.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
  @FXML
  public void showLeaderboard() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/leaderboardView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * A button action to show game create screen.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
  @FXML
  public void showGameCreation() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gameCreationView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * A button action to join a specific room.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   * @throws InterruptedException - Throws an exception when there is a problem with loading fxml
   *     file.
   * @throws URISyntaxException - Throws an exception when there is a problem with loading sound
   *     files.
   */
  @FXML
  public void joinRoom() throws IOException, InterruptedException, URISyntaxException {
    playSoundEffect(buttonSound);
    ObjectMapper objectMapper = new ObjectMapper();

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://group12-katan-backend.onrender.com/api/game?gameId=%s", lobbyIdLabel.getText())))
            .header("Content-Type", "application/json")
            .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
            .build();

    HttpResponse<String> response =
        HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    GameData gData = objectMapper.readValue(response.body(), GameData.class);

    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Success", "You joined into a lobby!");
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/roomView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    RoomController roomController = fxmlLoader.getController();
    roomController.initData(gData, Integer.toString(gData.getGameId()), false);
    stage.show();
  }

  /**
   * A button action to get back to the menu.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
  @FXML
  public void backToMenu() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
