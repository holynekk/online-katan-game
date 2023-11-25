package com.group12.helper;

import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationHelper {

  public static void showAlert(Alert.AlertType alertType, String title, String message) {
    if (alertType.name().equals(Alert.AlertType.ERROR.name())) {
      Notifications.create().title(title).text(message).hideAfter(Duration.seconds(5)).showError();
    } else if (alertType.name().equals(Alert.AlertType.INFORMATION.name())) {
      Notifications.create()
          .title(title)
          .text(message)
          .hideAfter(Duration.seconds(5))
          .showInformation();
    }
  }
}
