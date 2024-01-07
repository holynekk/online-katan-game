package com.group12.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.group12.helper.BackgroundHelper.parchmentBackgroundImage;
import static com.group12.helper.BackgroundHelper.setTheBackground;
import static com.group12.helper.MediaHelper.*;

/**
 * The {@code SettingsController} is responsible for managing the settings interface. This
 * controller allows users to adjust settings, background music volume and sound effects volume.
 *
 * <p>It uses {@link Slider} controls for adjusting volume levels and a {@link Button} to navigate
 * back to the main menu. The class also manages the initialization of these settings.
 */
@Component
public class SettingsController {

  @FXML private Button backButton;

  @FXML private Slider backgroundMusicSlider;

  @FXML private Slider effectsMusicSlider;

  @FXML private BorderPane borderpn;

  /**
   * A method to initialize slider values according to static variables of mediaPlayer.
   *
   * @throws URISyntaxException - Throws an exception when there is a problem with loading
   *     background images.
   */
  public void initialize() throws URISyntaxException {
    backgroundMusicSlider.setValue(backgroundPlayer.getVolume() * 100);
    backgroundMusicSlider
        .valueProperty()
        .addListener(
            observable -> {
              backgroundPlayer.setVolume(backgroundMusicSlider.getValue() / 100);
              backgroundPlayer2.setVolume(backgroundMusicSlider.getValue() / 100);
            });
    effectsMusicSlider.setValue(effectVolume);
    effectsMusicSlider
        .valueProperty()
        .addListener(observable -> effectVolume = effectsMusicSlider.getValue());
    setTheBackground(borderpn, parchmentBackgroundImage);
  }

  /**
   * A button action to get back to the menu.
   *
   * @throws IOException - Throws an exception when there is a problem with loading fxml file.
   */
  @FXML
  private void backToMenu() throws IOException {
    playSoundEffect(buttonSound);
    Stage stage = (Stage) backButton.getScene().getWindow();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
