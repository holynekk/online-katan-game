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
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class RegisterController {

  @FXML private TextField email;

  @FXML private TextField username;

  @FXML private TextField displayName;

  @FXML private TextField password;

  @FXML private TextField confirmPassword;

  @FXML private Button registerButton;

  Window window;

  @FXML
  private void register() throws IOException, InterruptedException {
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
      }
    }
  }

  private boolean isAlreadyRegistered() throws IOException, InterruptedException {
    boolean usernameExist = false;
    // Check if user is already exists

    return usernameExist;
  }

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
    } else if (isAlreadyRegistered()) {
      NotificationHelper.showAlert(
          Alert.AlertType.ERROR, "Error", "The username is already taken by someone else.");
      username.requestFocus();
    } else {
      return true;
    }
    return false;
  }

  @FXML
  private void showLoginScene() throws IOException {
    Stage stage = (Stage) registerButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
