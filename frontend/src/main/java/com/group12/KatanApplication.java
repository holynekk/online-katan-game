package com.group12;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import static com.group12.helper.HttpClientHelper.getAllSessionCookies;
import static com.group12.helper.HttpClientHelper.getSessionCookie;
import static com.group12.helper.StompClient.exitGame;

public class KatanApplication extends Application {

  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() {
    applicationContext = new SpringApplicationBuilder(KatanFrontendApplication.class).run();
  }

  @Override
  public void start(Stage stage) {
    applicationContext.publishEvent(new StageReadyEvent(stage));
  }

  @Override
  public void stop() throws JsonProcessingException {
    exitGame(getSessionCookie("gameId"));
    applicationContext.close();
    Platform.exit();
  }

  public static class StageReadyEvent extends ApplicationEvent {
    public StageReadyEvent(Stage stage) {
      super(stage);
    }

    public Stage getStage() {
      return ((Stage) getSource());
    }
  }
}
