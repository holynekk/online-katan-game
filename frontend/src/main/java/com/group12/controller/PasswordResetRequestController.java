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

@Component
public class PasswordResetRequestController {

  @FXML private Button sendRequestButton;

  @FXML private TextField emailField;

  @FXML private BorderPane borderpn;

  public void initialize() throws URISyntaxException {
    setTheBackground(borderpn, menuBackgroundImage);
  }

  @FXML
  public void sendRequest() throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "http://localhost:8080/api/auth/password-reset-request?email=%s",
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

  public void passwordResetScene() throws IOException {
    Stage stage = (Stage) sendRequestButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/resetPasswordView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void showLoginScene() throws IOException {
    Stage stage = (Stage) sendRequestButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
