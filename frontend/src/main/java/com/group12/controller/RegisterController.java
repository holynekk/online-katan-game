package com.group12.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.helper.NotificationHelper;
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
import java.util.HashMap;
import java.util.Map;

import static com.group12.helper.BackgroundHelper.menuBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.*;

/**
 * The {@code RegisterController} is a Spring component that manages the user interface and
 * interactions for the registration process in an application. This controller is responsible for
 * handling user input for registration details such as email, username, display name, and password.
 * It validates the input, sends registration requests to the server, and handles the response.
 */
@Component
public class RegisterController {

  @FXML private TextField email;

  @FXML private TextField username;

  @FXML private TextField displayName;

  @FXML private TextField password;

  @FXML private TextField confirmPassword;

  @FXML private Button registerButton;

  @FXML private BorderPane borderpn;

  Window window;

  /**
   * An initialize method to set the background.
   *
   * @throws URISyntaxException - Throws an exception when there is a problem with loading *
   *     background images.
   */
  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, menuBackgroundImage);
  }

  /**
   * A button action to send request to the server with the data of register form.
   *
   * @throws IOException - exception of json serialize/deserialize function.
   * @throws InterruptedException - Throws an exception.
   */
  @FXML
  private void register() throws IOException, InterruptedException {
    playSoundEffect(buttonSound);
    window = registerButton.getScene().getWindow();

    if (this.isValidated()) {
      ObjectMapper objectMapper = new ObjectMapper();

      Map<String, String> body = new HashMap<>();
      body.put("username", username.getText());
      body.put("password", password.getText());
      body.put("email", email.getText());
      body.put("displayName", displayName.getText());

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8080/api/user"))
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
              .build();

      HttpResponse<String> response =
          HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 201) {
        NotificationHelper.showAlert(
            Alert.AlertType.INFORMATION, "Success", "New user has been created!");
        showLoginScene();
      } else if (response.statusCode() == 400) {
        NotificationHelper.showAlert(Alert.AlertType.ERROR, "Error", response.body());
      }
    }
  }

  /**
   * A method to validate the given credentials on the textFields.
   *
   * @return - Boolean value if the all form is valid or not.
   * @throws IOException - Throws exception.
   * @throws InterruptedException - Throws exception.
   */
  private boolean isValidated() throws IOException, InterruptedException {
    window = registerButton.getScene().getWindow();
    if (email.getText().isEmpty()) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Email text field cannot be blank.");
      email.requestFocus();
    } else if (username.getText().isEmpty()) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Username text field cannot be blank.");
      username.requestFocus();
    } else if (displayName.getText().isEmpty()) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Username text field cannot be blank.");
      displayName.requestFocus();
    } else if (password.getText().isEmpty()) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Password text field cannot be blank.");
      password.requestFocus();
    } else if (confirmPassword.getText().isEmpty()) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "Confirm password text field cannot be blank.");
      confirmPassword.requestFocus();
    } else if (!password.getText().equals(confirmPassword.getText())) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR,
          "Error",
          "Password and confirm password text fields does not match.");
      password.requestFocus();
    } else {
      return true;
    }
    return false;
  }

  /**
   * A button action to get back to the login screen.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
  @FXML
  private void showLoginScene() throws IOException {
    Stage stage = (Stage) registerButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
