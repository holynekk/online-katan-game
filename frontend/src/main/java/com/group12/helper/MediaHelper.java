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
  public static final String inGameBackgroundMusic = "in_game_background.mp3";
  public static final String diceEffect = "dice_roll.mp3";
  public static final String buttonSound = "button_sound.mp3";
  public static final String turnSound = "turn_effect.mp3";
  public static final String victoriousSound = "victorious_sound.mp3";
  public static final String defeatedSound = "defeated_sound.mp3";

  /**
   * Constructor of the global media player for the application. Plays the main background music in
   * loop at the very first boot.
   *
   * @throws URISyntaxException - throws an error when track does not exist in the provided
   *     location.
   */
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
    backgroundPlayer.setOnEndOfMedia(() -> backgroundPlayer.seek(Duration.ZERO));
    backgroundPlayer.play();
  }

  /**
   * A method to change background music.
   *
   * @param music - Music name parameter
   */
  public static void switchBackgroundMusic(String music) {
    backgroundPlayer.stop();
    Media sound = null;

    try {
      sound =
          new Media(MediaHelper.class.getResource("../../../sounds/" + music).toURI().toString());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    backgroundPlayer = new MediaPlayer(sound);
    backgroundPlayer.setVolume(0.2);
    backgroundPlayer.setOnEndOfMedia(() -> backgroundPlayer.seek(Duration.ZERO));
    backgroundPlayer.play();
  }

  /**
   * A static helper method to play sound effects throughout the application.
   *
   * @param effect - effect name
   */
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
