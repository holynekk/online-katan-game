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

/**
 * The {@code PasswordResetController} class is responsible for handling the user interface and
 * logic for resetting a user's password. This includes verifying the token, setting a new password,
 * and displaying appropriate notifications.
 */
@Component
public class PasswordResetController {

  @FXML private TextField tokenField;

  @FXML private PasswordField passwordField;

  @FXML private PasswordField confirmPasswordField;

  @FXML private Button resetPasswordButton;

  @FXML private BorderPane borderpn;

  /**
   * Initializes the controller. Sets the background of the password reset view.
   *
   * @throws URISyntaxException if the URI for the background image is incorrect.
   */
  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, menuBackgroundImage);
  }

  /**
   * Handles the password reset process. This method is called when the reset password button is
   * clicked. It sends a password reset request to the server with the provided token and new
   * password. Displays success or error notifications based on the server response.
   *
   * @throws IOException if there's an error in sending the HTTP request or loading the login scene.
   * @throws InterruptedException if the HTTP client is interrupted while sending a request.
   */
  @FXML
  public void resetPassword() throws IOException, InterruptedException {
    if (passwordField.getText().equals(confirmPasswordField.getText())) {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(
                  URI.create(
                      String.format(
                          "https://group12-katan-backend.onrender.com/api/auth/set-new-password?token=%s&password=%s",
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

  /**
   * Transitions to the login scene. This method is invoked after a successful password reset or
   * when the user chooses to return to the login screen.
   *
   * @throws IOException if there's an error in loading the login view FXML.
   */
  @FXML
  public void showLoginScene() throws IOException {
    Stage stage = (Stage) resetPasswordButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
