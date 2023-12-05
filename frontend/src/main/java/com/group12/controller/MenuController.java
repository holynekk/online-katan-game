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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

@Component
public class MenuController {

  @FXML private Button playButton;

  @FXML private Button leaderboardButton;

  @FXML private Button settingsButton;

  @FXML private Button logoutButton;

  @FXML private Button userProfileButton;

  @FXML private BorderPane borderpn;

  private String nextScene;

  public void initialize() throws URISyntaxException {
    userProfileButton.setText(getSessionCookie("username"));
    Image image =
        new Image(getClass().getResource("../../../assets/menu_background.jpg").toURI().toString());
    BackgroundImage backgroundImage =
        new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, true, true));
    Background bg = new Background(backgroundImage);
    borderpn.setBackground(bg);
  }

  @FXML
  private void changeMenuScene(ActionEvent event) throws IOException, InterruptedException {
    final Node source = (Node) event.getSource();
    String id = source.getId();

    playSoundEffect(buttonSound);
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
              .header("X-CSRF", getSessionCookie("X-CSRF"))
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

  @FXML
  private void openUserProfile() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) userProfileButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/userProfileView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
