package com.group12.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.model.ScoreData;
import com.group12.model.ScoreModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

/**
 * The {@code LeaderboardController} class manages the leaderboard UI and its functionalities. This
 * controller handles the display of the leaderboard, including score data and player rankings.
 *
 * <p>The leaderboard data is fetched from a REST API and displayed in a TableView.
 */
@Component
public class LeaderboardController {

  @FXML private Button backButton;

  @FXML TableView<ScoreModel> scoreTableView;

  @FXML private TableColumn<ScoreModel, String> displayNameColumn;

  @FXML private TableColumn<ScoreModel, Integer> totalWinsColumn;

  @FXML private TableColumn<ScoreModel, Integer> totalScoreColumn;

  @FXML MenuButton timeInterval;

  @FXML private BorderPane borderpn;

  private String tmInterval;

  private ObservableList<ScoreModel> scores;

  /**
   * Initializes the controller. Sets up the table columns and populates the leaderboard. Also sets
   * the background for the leaderboard view. This method is automatically called after the FXML
   * fields have been injected.
   *
   * @throws IOException if there's an error in loading data for the leaderboard.
   * @throws InterruptedException if the thread is interrupted during an ongoing operation.
   * @throws URISyntaxException if the URI for the background image is incorrect.
   */
  public void initialize() throws IOException, InterruptedException, URISyntaxException {
    tmInterval = "All";
    displayNameColumn.setCellValueFactory(
        new PropertyValueFactory<ScoreModel, String>("displayName"));
    totalWinsColumn.setCellValueFactory(new PropertyValueFactory<ScoreModel, Integer>("totalWins"));
    totalScoreColumn.setCellValueFactory(
        new PropertyValueFactory<ScoreModel, Integer>("totalScore"));
    populateTable();
    setTheBackground(borderpn, parchmentBackgroundImage);
  }

  /**
   * Handles the action to go back to the main menu. Called when the back button is pressed.
   *
   * @throws IOException if there's an error in loading the main menu view.
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

  /**
   * Changes the time interval for the leaderboard based on user selection. Called when a different
   * time interval is selected from the menu.
   *
   * @param event The action event triggered by selecting a time interval.
   * @throws IOException if there's an error in fetching data for the new time interval.
   * @throws InterruptedException if the thread is interrupted during an ongoing operation.
   */
  @FXML
  public void changeTimeInterval(ActionEvent event) throws IOException, InterruptedException {
    playSoundEffect(buttonSound);
    final MenuItem source = (MenuItem) event.getSource();
    timeInterval.setText(source.getText());
    tmInterval = source.getText().toLowerCase();
    scoreTableView.getItems().clear();
    populateTable();
  }

  /**
   * Populates the leaderboard table with data. Fetches the leaderboard data from the server based
   * on the selected time interval and updates the table view.
   *
   * @throws IOException if there's an error in fetching leaderboard data.
   * @throws InterruptedException if the thread is interrupted during an ongoing operation.
   */
  public void populateTable() throws IOException, InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> body = new HashMap<>();
    body.put("timeInterval", tmInterval);
    body.put("offset", 0);
    body.put("pagination", 10);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("https://group12-katan-backend.onrender.com/api/leaderboard"))
            .header("Content-Type", "application/json")
            .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build();

    HttpResponse<String> response =
        HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      List<ScoreData> scoreList =
          objectMapper.readValue(response.body(), new TypeReference<List<ScoreData>>() {});
      for (ScoreData data : scoreList) {
        scoreTableView
            .getItems()
            .add(new ScoreModel(data.getDisplayName(), data.getTotalWins(), data.getTotalScore()));
      }
    }
  }
}
