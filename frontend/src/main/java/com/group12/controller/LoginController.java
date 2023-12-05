package com.group12.controller;

import com.group12.helper.NotificationHelper;
import com.group12.helper.HttpClientHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static com.group12.helper.BackgroundHelper.menuBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.*;

@Component
public class LoginController {

  @FXML private TextField username;

  @FXML private TextField password;

  @FXML private Button loginButton;

  @FXML private BorderPane borderpn;

  Window window;

  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, menuBackgroundImage);
  }

  @FXML
  private void login() throws Exception {
    playSoundEffect(buttonSound);
    if (this.isValid()) {

      String valueToEncode = username.getText() + ":" + password.getText();
      String basicAuthString =
          "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8080/api/auth/login"))
              .header("Authorization", basicAuthString)
              .build();
      HttpResponse<String> response =
          HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        NotificationHelper.showAlert(
            Alert.AlertType.INFORMATION, "Success", "You successfully logged in!");
        HttpClientHelper.addNewSessionCookie("X-CSRF", response.body());
        HttpClientHelper.addNewSessionCookie("username", username.getText());
        HttpClientHelper.addNewSessionCookie(
            "userId", response.headers().allValues("userId").get(0));
        this.showMenuScene();
      } else if (response.statusCode() == 400) {
        NotificationHelper.showAlert(
            Alert.AlertType.ERROR, "Error", "Username or password is wrong! Please try again.");
        username.requestFocus();
      }
    }
  }

  private boolean isValid() {

    window = loginButton.getScene().getWindow();
    if (username.getText().isEmpty()) {
      NotificationHelper.showAlert(Alert.AlertType.ERROR, "Error", "Username cannot be empty.");
      username.requestFocus();
    } else if (password.getText().isEmpty()) {
      NotificationHelper.showAlert(Alert.AlertType.ERROR, "Error", "Password cannot be empty.");
      password.requestFocus();
    } else {
      return true;
    }
    return false;
  }

  @FXML
  private void showRegisterScene() throws IOException {
    Stage stage = (Stage) loginButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/registerView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  private void showMenuScene() throws IOException {
    Stage stage = (Stage) loginButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
