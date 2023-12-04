package com.group12.helper;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class MediaHelper {
    public static MediaPlayer mediaPlayer;

    public MediaHelper() throws URISyntaxException {
        String musicFile = "aeo2_menu.mp3";

        Media sound = null;
        try {
            sound = new Media(getClass().getResource("../../../sounds/" + musicFile).toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();

    }

    public static void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }
}
