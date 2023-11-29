package com.group12.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.model.ScoreData;
import com.group12.model.ScoreModel;
import com.sun.jdi.PrimitiveValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class LeaderboardController {

  @FXML private Button backButton;

  @FXML TableView<ScoreModel> scoreTableView;

  @FXML private TableColumn<ScoreModel, String> displayNameColumn;

  @FXML private TableColumn<ScoreModel, Integer> totalWinsColumn;

  @FXML private TableColumn<ScoreModel, Integer> totalScoreColumn;

  @FXML MenuButton timeInterval;

  private String tmInterval;

  private ObservableList<ScoreModel> scores;

  public void initialize() throws IOException, InterruptedException {
    tmInterval = "All";
    displayNameColumn.setCellValueFactory(
        new PropertyValueFactory<ScoreModel, String>("displayName"));
    totalWinsColumn.setCellValueFactory(new PropertyValueFactory<ScoreModel, Integer>("totalWins"));
    totalScoreColumn.setCellValueFactory(
        new PropertyValueFactory<ScoreModel, Integer>("totalScore"));
    populateTable();
  }

  @FXML
  public void backToMenu() throws IOException {
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void changeTimeInterval(ActionEvent event) throws IOException, InterruptedException {
    final MenuItem source = (MenuItem) event.getSource();
    timeInterval.setText(source.getText());
    tmInterval = source.getText().toLowerCase();
    scoreTableView.getItems().clear();
    populateTable();
  }

  public void populateTable() throws IOException, InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> body = new HashMap<>();
    body.put("timeInterval", tmInterval);
    body.put("offset", 0);
    body.put("pagination", 10);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/leaderboard"))
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
            .add(
                new ScoreModel(
                    data.getDisplayName(),
                    data.getTotalWins(),
                    data.getTotalScore()));
      }
    }
  }
}
