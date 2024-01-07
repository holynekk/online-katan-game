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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.group12.helper.BackgroundHelper.menuBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

/**
 * The {@code MenuController} class is responsible for handling the interactions in the main menu of
 * the application. This includes navigating between different scenes like play, leaderboard,
 * settings, user profile, and logout.
 */
@Component
public class MenuController {

  @FXML private Button playButton;

  @FXML private Button leaderboardButton;

  @FXML private Button settingsButton;

  @FXML private Button logoutButton;

  @FXML private Button userProfileButton;

  @FXML private BorderPane borderpn;

  private String nextScene;

  /**
   * An initialize method to set the background.
   *
   * @throws URISyntaxException - Throws an exception when there is a problem with loading *
   *     background images.
   */
  public void initialize() throws URISyntaxException {
    userProfileButton.setText(getSessionCookie("username"));
    setTheBackground(borderpn, menuBackgroundImage);
  }

  /**
   * A button action to load the relevant scene according to the button id.
   *
   * @param event - Button action event.
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   * @throws InterruptedException - Throws an exception when there is a problem with loading fxml
   *     file.
   */
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
              .uri(URI.create("https://group12-katan-backend.onrender.com/api/auth/logout"))
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

  /**
   * A button action to load user profile screen.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
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
