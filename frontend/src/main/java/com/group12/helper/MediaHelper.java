package com.group12.helper;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class MediaHelper {
  public static MediaPlayer backgroundPlayer;
  public static MediaPlayer effectsPlayer;
  public static double effectVolume;

  public static final String menuBackgroundMusic = "aeo2_menu.mp3";
  public static final String diceEffect = "dice_roll.mp3";
  public static final String buttonSound = "button_sound.mp3";

  public MediaHelper() throws URISyntaxException {
    effectVolume = 100;

    Media sound = null;

    try {
      sound =
          new Media(
              getClass().getResource("../../../sounds/" + menuBackgroundMusic).toURI().toString());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    backgroundPlayer = new MediaPlayer(sound);
    backgroundPlayer.setVolume(0.2);
    backgroundPlayer.setOnEndOfMedia(
        new Runnable() {
          @Override
          public void run() {
            backgroundPlayer.seek(Duration.ZERO);
          }
        });
    backgroundPlayer.play();
  }

  public static void playSoundEffect(String effect) {
    Media sound = null;
    try {
      sound =
          new Media(MediaHelper.class.getResource("../../../sounds/" + effect).toURI().toString());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    effectsPlayer = new MediaPlayer(sound);
    effectsPlayer.setVolume(effectVolume / 100);
    effectsPlayer.play();
  }
}
