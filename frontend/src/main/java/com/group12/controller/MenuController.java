package com.group12.controller;

import com.group12.helper.HttpClientHelper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class MenuController {

  @FXML private Button logoutButton;

  @FXML
  private void logout() throws IOException, InterruptedException {

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/auth/logout"))
            .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
            .DELETE()
            .build();

    HttpResponse<String> response =
        HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

    HttpClientHelper.removeAllSessionCookies();

    Stage stage = (Stage) logoutButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
