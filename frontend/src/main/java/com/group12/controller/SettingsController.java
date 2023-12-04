package com.group12.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.group12.helper.MediaHelper.mediaPlayer;

@Component
public class SettingsController {

    @FXML private Button backButton;

    @FXML private Slider backgroundMusicSlider;

    public void initialize() {
        backgroundMusicSlider.setValue(mediaPlayer.getVolume() * 100);
        backgroundMusicSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(backgroundMusicSlider.getValue() / 100);
            }
        });
    }

    @FXML
    private void backToMenu() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }


}
