package com.group12.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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

@Component
public class SettingsController {

  @FXML private Button backButton;

  @FXML private Slider backgroundMusicSlider;

  @FXML private Slider effectsMusicSlider;

  @FXML private BorderPane borderpn;

  public void initialize() throws URISyntaxException {
    backgroundMusicSlider.setValue(backgroundPlayer.getVolume() * 100);
    backgroundMusicSlider
        .valueProperty()
        .addListener(
            new InvalidationListener() {
              @Override
              public void invalidated(Observable observable) {
                backgroundPlayer.setVolume(backgroundMusicSlider.getValue() / 100);
              }
            });
    effectsMusicSlider.setValue(effectVolume);
    effectsMusicSlider
        .valueProperty()
        .addListener(
            new InvalidationListener() {
              @Override
              public void invalidated(Observable observable) {
                effectVolume = effectsMusicSlider.getValue();
              }
            });
    setTheBackground(borderpn, parchmentBackgroundImage);
  }

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
