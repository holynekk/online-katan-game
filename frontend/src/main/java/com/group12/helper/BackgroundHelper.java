package com.group12.helper;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URISyntaxException;

public class BackgroundHelper {

  public static String menuBackgroundImage = "menu_background.jpg";

  public static String parchmentBackgroundImage = "parchment.jpg";

  /**
   * A static method to set a background for a specific scene.
   *
   * @param borderPane - Current scene's pane element.
   * @param backgroundName - Background image name which will be used in the scene.
   * @throws URISyntaxException - throws an error when specified location of the image is empty.
   */
  public static void setTheBackground(BorderPane borderPane, String backgroundName)
      throws URISyntaxException {
    Image image =
        new Image(
            BackgroundHelper.class
                .getResource("../../../assets/" + backgroundName)
                .toURI()
                .toString());
    BackgroundImage backgroundImage =
        new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, true, true));
    Background bg = new Background(backgroundImage);
    borderPane.setBackground(bg);
  }
}
