package com.group12.controller;

import com.group12.helper.HttpClientHelper;
import com.group12.helper.NotificationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
 * The {@code PasswordResetRequestController} class is responsible for handling the user interface
 * for requesting a password reset. This includes capturing user input for email, sending a password
 * reset request, and navigating between the password reset request scene and the login scene. The
 * class communicates with a server-side API to handle the password reset request process.
 */
@Component
public class PasswordResetRequestController {

  @FXML private Button sendRequestButton;

  @FXML private TextField emailField;

  @FXML private BorderPane borderpn;

  /**
   * Initializes the controller. Sets the background of the password reset request view.
   *
   * @throws URISyntaxException if the URI for the background image is incorrect.
   */
  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, menuBackgroundImage);
  }

  /**
   * Handles sending a password reset request to the server. This method is called when the send
   * request button is clicked. It sends an HTTP request to the server with the provided email and
   * displays appropriate notifications.
   *
   * @throws IOException if there's an error in sending the HTTP request or loading the next scene.
   * @throws InterruptedException if the HTTP client is interrupted while sending a request.
   */
  @FXML
  public void sendRequest() throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://group12-katan-backend.onrender.com/api/auth/password-reset-request?email=%s",
                        emailField.getText())))
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .build();
    HttpResponse<String> response =
        HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Success", response.body());
      passwordResetScene();
    } else {
      NotificationHelper.showAlert(Alert.AlertType.INFORMATION, "Error", response.body());
    }
  }

  /**
   * Transitions to the password reset scene. This method is invoked after a successful password
   * reset request.
   *
   * @throws IOException if there's an error in loading the password reset view FXML.
   */
  public void passwordResetScene() throws IOException {
    Stage stage = (Stage) sendRequestButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/resetPasswordView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Navigates back to the login scene. This method is called when the user chooses to return to the
   * login screen.
   *
   * @throws IOException if there's an error in loading the login view FXML.
   */
  @FXML
  public void showLoginScene() throws IOException {
    Stage stage = (Stage) sendRequestButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
