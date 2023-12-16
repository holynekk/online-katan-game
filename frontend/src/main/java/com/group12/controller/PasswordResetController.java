package com.group12.controller;

import com.group12.helper.HttpClientHelper;
import com.group12.helper.NotificationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.group12.helper.BackgroundHelper.menuBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;

@Component
public class PasswordResetController {

  @FXML private TextField tokenField;

  @FXML private PasswordField passwordField;

  @FXML private PasswordField confirmPasswordField;

  @FXML private Button resetPasswordButton;

  @FXML private BorderPane borderpn;

  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, menuBackgroundImage);
  }

  @FXML
  public void resetPassword() throws IOException, InterruptedException {
    if (passwordField.getText().equals(confirmPasswordField.getText())) {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(
                  URI.create(
                      String.format(
                          "http://localhost:8080/api/auth/set-new-password?token=%s&password=%s",
                          tokenField.getText(), passwordField.getText())))
              .build();
      HttpResponse<String> response =
          HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Success", response.body());
        showLoginScene();
      } else {
        NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Error", response.body());
      }
    } else {
      NotificationHelper.showAlert(
          Alert.AlertType.INFORMATION, "Error", "Entered passwords does not match!");
      passwordField.requestFocus();
    }
  }

  @FXML
  public void showLoginScene() throws IOException {
    Stage stage = (Stage) resetPasswordButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
