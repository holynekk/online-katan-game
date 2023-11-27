package com.group12.controller;

import com.group12.model.ScoreModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class LeaderboardController {

  @FXML private Button backButton;

  @FXML
  TableView<ScoreModel> scoreTableView;

  private ObservableList<ScoreModel> scores;

  public void initialize() {
    ScoreModel score1 = new ScoreModel("a", 5);
    ScoreModel score2 = new ScoreModel("b", 25);
    ScoreModel score3 = new ScoreModel("c", 51);
    ScoreModel score4 = new ScoreModel("d", 55);
    scoreTableView.getItems().add(score1);
    scoreTableView.getItems().add(score2);
    scoreTableView.getItems().add(score3);
    scoreTableView.getItems().add(score4);

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
