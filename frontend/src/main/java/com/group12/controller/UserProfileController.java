package com.group12.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.helper.HttpClientHelper;
import com.group12.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.buttonSound;
import static com.group12.helper.MediaHelper.playSoundEffect;

/**
 * The {@code UserProfileController} manages the user profile interface. It is responsible for
 * displaying user-specific information such as user ID, display name, email, and game history.
 */
@Component
public class UserProfileController {

  @FXML private Button backButton;

  @FXML private Label userIdLabel;

  @FXML private Label displayNameLabel;

  @FXML private Label emailLabel;

  @FXML private Label lastNameLabel;

  @FXML private Label firstNameLabel;

  @FXML private TableColumn<GameHistoryModel, Integer> gameIdColumn;

  @FXML private TableColumn<GameHistoryModel, String> historyColumn;

  @FXML private TableColumn<GameHistoryModel, String> didWonColumn;

  @FXML private TableColumn<GameHistoryModel, Integer> scoreColumn;

  @FXML TableView<GameHistoryModel> gameHistoryTableView;

  @FXML private BorderPane borderpn;

  @FXML private ImageView userProfileImage;

  /**
   * An initialize method to populate tables, user information, background image, etc.
   *
   * @throws IOException - Throws an exception when there is a problem with loading * * background
   *     images.
   * @throws InterruptedException - Throws an exception when there is a problem with loading * * *
   *     background images.
   * @throws URISyntaxException - Throws an exception when there is a problem with loading * * *
   *     background images.
   */
  public void initialize() throws IOException, InterruptedException, URISyntaxException {
    setTheBackground(borderpn, parchmentBackgroundImage);
    File file = new File("src/main/resources/assets/villager.png");
    userProfileImage.setImage(new Image(file.toURI().toString()));
    userProfileImage.setFitHeight(100);
    userProfileImage.setFitWidth(100);

    ObjectMapper objectMapper = new ObjectMapper();

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://group12-katan-backend.onrender.com/api/user?username=%s",
                        HttpClientHelper.getSessionCookie("username"))))
            .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
            .build();

    HttpResponse<String> response =
        HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

    UserData userData = objectMapper.readValue(response.body(), new TypeReference<UserData>() {});

    userIdLabel.setText(Integer.toString(userData.getUserId()));
    displayNameLabel.setText(userData.getDisplayName());
    emailLabel.setText(userData.getEmail());
    firstNameLabel.setText(userData.getFirstName());
    lastNameLabel.setText(userData.getLastName());

    gameIdColumn.setCellValueFactory(new PropertyValueFactory<GameHistoryModel, Integer>("gameId"));
    historyColumn.setCellValueFactory(
        new PropertyValueFactory<GameHistoryModel, String>("history"));
    didWonColumn.setCellValueFactory(new PropertyValueFactory<GameHistoryModel, String>("didWon"));
    scoreColumn.setCellValueFactory(new PropertyValueFactory<GameHistoryModel, Integer>("score"));

    request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://group12-katan-backend.onrender.com/api/user/game-history?username=%s",
                        HttpClientHelper.getSessionCookie("username"))))
            .header("X-CSRF", HttpClientHelper.getSessionCookie("X-CSRF"))
            .build();
    response = HttpClientHelper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      List<GameHistoryData> scoreList =
          objectMapper.readValue(response.body(), new TypeReference<List<GameHistoryData>>() {});
      for (GameHistoryData data : scoreList) {
        gameHistoryTableView
            .getItems()
            .add(
                new GameHistoryModel(
                    data.getGameId(),
                    data.getHistory().toString(),
                    data.isDidWon() ? "Won" : "Lost",
                    data.getScore()));
      }
    }
  }

  /**
   * A button action to get back to the menu screen.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
  @FXML
  public void backToMenu() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
