package com.group12.helper;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * The {@code MediaHelper} class is to manage and play media assets like background music and sound
 * effects. It handles the initialization and control of media players.
 */
@Component
public class MediaHelper {
  public static MediaPlayer backgroundPlayer;
  public static MediaPlayer backgroundPlayer2;
  public static double effectVolume;

  public static final String menuBackgroundMusic = "aeo2_menu.mp3";
  public static final String inGameBackgroundMusic = "in_game_background.mp3";
  public static final String diceEffect = "dice_roll.mp3";
  public static final String buttonSound = "button_sound.mp3";
  public static final String buildSound = "build_sound.mp3";
  public static final String turnSound = "turn_effect.mp3";
  public static final String victoriousSound = "victorious_sound.mp3";
  public static final String defeatedSound = "defeated_sound.mp3";

  public static MediaPlayer dicePlayer;
  public static MediaPlayer buttonPlayer;
  public static MediaPlayer buildPlayer;
  public static MediaPlayer turnPlayer;
  public static MediaPlayer victoriousPlayer;
  public static MediaPlayer defeatedPlayer;

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

    try {
      sound =
          new Media(
              MediaHelper.class
                  .getResource("../../../sounds/" + inGameBackgroundMusic)
                  .toURI()
                  .toString());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    backgroundPlayer2 = new MediaPlayer(sound);
    backgroundPlayer2.setVolume(0.2);
    backgroundPlayer2.setOnEndOfMedia(() -> backgroundPlayer2.seek(Duration.ZERO));

    initEffectPlayers();
    backgroundPlayer.play();
  }

  /**
   * A method to switch background musics while going in and out game lobbies.
   *
   * @param isInGame - A boolean field to specify the background music.
   */
  public static void switchBackgroundMusic(Boolean isInGame) {
    if (isInGame) {
      backgroundPlayer.stop();
      backgroundPlayer2.seek(Duration.ZERO);
      backgroundPlayer2.play();
    } else {
      backgroundPlayer2.stop();
      backgroundPlayer.seek(Duration.ZERO);
      backgroundPlayer.play();
    }
  }

  /**
   * A static helper method to play sound effects throughout the application.
   *
   * @param effect - effect name
   */
  public static void playSoundEffect(String effect) {
    switch (effect) {
      case diceEffect:
        dicePlayer.seek(Duration.ZERO);
        dicePlayer.setVolume(effectVolume / 100);
        dicePlayer.play();
        break;
      case buttonSound:
        buttonPlayer.seek(Duration.ZERO);
        buttonPlayer.setVolume(effectVolume / 100);
        buttonPlayer.play();
        break;
      case buildSound:
        buildPlayer.seek(Duration.ZERO);
        buildPlayer.setVolume(effectVolume / 100);
        buildPlayer.play();
        break;
      case turnSound:
        turnPlayer.seek(Duration.ZERO);
        turnPlayer.setVolume(effectVolume / 100);
        turnPlayer.play();
        break;
      case victoriousSound:
        victoriousPlayer.seek(Duration.ZERO);
        victoriousPlayer.setVolume(effectVolume / 100);
        victoriousPlayer.play();
        break;
      case defeatedSound:
        defeatedPlayer.seek(Duration.ZERO);
        defeatedPlayer.setVolume(effectVolume / 100);
        defeatedPlayer.play();
        break;
      default:
        break;
    }
  }

  /** Method to initialize all sound effect players. */
  public void initEffectPlayers() {
    Media sound = null;
    try {
      sound =
          new Media(
              MediaHelper.class.getResource("../../../sounds/" + diceEffect).toURI().toString());
      dicePlayer = new MediaPlayer(sound);

      sound =
          new Media(
              MediaHelper.class.getResource("../../../sounds/" + buttonSound).toURI().toString());
      buttonPlayer = new MediaPlayer(sound);

      sound =
          new Media(
              MediaHelper.class.getResource("../../../sounds/" + buildSound).toURI().toString());
      buildPlayer = new MediaPlayer(sound);

      sound =
          new Media(
              MediaHelper.class.getResource("../../../sounds/" + turnSound).toURI().toString());
      turnPlayer = new MediaPlayer(sound);

      sound =
          new Media(
              MediaHelper.class
                  .getResource("../../../sounds/" + victoriousSound)
                  .toURI()
                  .toString());
      victoriousPlayer = new MediaPlayer(sound);

      sound =
          new Media(
              MediaHelper.class.getResource("../../../sounds/" + defeatedSound).toURI().toString());
      defeatedPlayer = new MediaPlayer(sound);

    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
