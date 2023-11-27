package com.group12.controller;

import com.group12.helper.HttpClientHelper;

import com.group12.helper.NotificationHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class MenuController {

  @FXML private Button playButton;

  @FXML private Button leaderboardButton;

  @FXML private Button settingsButton;

  @FXML private Button logoutButton;

  private String nextScene;

  @FXML
  private void changeMenuScene(ActionEvent event) throws IOException, InterruptedException {
    final Node source = (Node) event.getSource();
    String id = source.getId();

    if (id.equals(playButton.getId())) {
      nextScene = "/view/lobbyView.fxml";
    } else if (id.equals(leaderboardButton.getId())) {
      nextScene = "/view/leaderboardView.fxml";
    } else if (id.equals(settingsButton.getId())) {
      nextScene = "/view/settingsView.fxml";
    } else if (id.equals(logoutButton.getId())) {
      nextScene = "/view/loginView.fxml";
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8080/api/auth/logout"))
              .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
              .DELETE()
              .build();

      HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
      HttpClientHelper.removeAllSessionCookies();
      NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "You are logged out!");
    }

    Stage stage = (Stage) logoutButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(nextScene));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
