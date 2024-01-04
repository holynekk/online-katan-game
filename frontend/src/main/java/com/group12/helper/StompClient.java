package com.group12.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.controller.OnlineGameController;
import com.group12.controller.RoomController;
import com.group12.model.chat.Message;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StompClient implements StompSessionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(StompClient.class);

  private static final String ENDPOINT_URL = "ws://localhost:8080/ws";

  private static StompSession stompSession;
  public static WebSocketStompClient stompClient;
  private String stompUsername;

  private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(false);
  private final ObservableList<Message> greetings = FXCollections.observableArrayList();

  private final ObjectMapper objectMapper;
  private final RoomController roomController;
  private OnlineGameController gameController;

  public StompClient(RoomController roomController, String stompUsername) {
    this.objectMapper = new ObjectMapper();
    this.roomController = roomController;
    this.stompUsername = stompUsername;

    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(webSocketClient));
    SockJsClient sockJsClient = new SockJsClient(transports);
    stompClient = new WebSocketStompClient(sockJsClient);

    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.afterPropertiesSet();

    stompClient.setMessageConverter(new StringMessageConverter());
    stompClient.setTaskScheduler(taskScheduler);
    stompClient.setDefaultHeartbeat(new long[] {0, 0});
  }

  public void setGameController(OnlineGameController controller) {
    this.gameController = controller;
  }

  public ObservableList<Message> getGreetings() {
    return greetings;
  }

  public ReadOnlyBooleanProperty connectedProperty() {
    return connected.getReadOnlyProperty();
  }

  public void connect() {
    try {
      stompSession = stompClient.connectAsync(ENDPOINT_URL, this).get();
    } catch (Exception e) {
      LOG.error("Connection failed.", e);
    }
  }

  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    LOG.info(
        " Connection to STOMP server established Session: {} Headers: {}",
        session.getSessionId(),
        connectedHeaders);

    subscribe("/topic/room");
    subscribe("/topic/chat");

    Platform.runLater(() -> connected.set(true));
  }

  public void subscribe(String topic) {
    LOG.info("Subscribing to topic {} for session {}", topic, stompSession.getSessionId());
    stompSession.subscribe(topic, this);
  }

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    Message msg = null;
    try {
      String strPayload = (String) payload;
      msg = objectMapper.readValue(strPayload, Message.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Message finalMsg = msg;
    switch (msg.getMsgType()) {
      case LOBBY_CHAT:
        Platform.runLater(() -> roomController.addChatMessage(finalMsg));
        break;
      case IN_GAME_CHAT:
        Platform.runLater(() -> gameController.addChatMessage(finalMsg));
        break;
      case USER_JOINED:
        Platform.runLater(
            () -> {
              roomController.refreshPlayerList(finalMsg);
              String[] playerUsernameList = finalMsg.getContent().split("/");
              roomController.addChatMessage(
                  playerUsernameList[playerUsernameList.length - 1]
                      + " has joined into the lobby!");
            });
        break;
      case READY:
        Platform.runLater(
            () -> {
              roomController.toggleStartGameButton(finalMsg.getContent().equals("true"));
              roomController.setReadyColor(finalMsg.getNickname());
              roomController.addChatMessage(finalMsg.getNickname() + " is ready!");
            });
        break;
      case START_GAME:
        Platform.runLater(
            () -> {
              try {
                roomController.showGameScene();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
        Platform.runLater(
            () -> {
              gameController.setBoard(finalMsg.getContent());
              gameController.setupHelper(finalMsg.getTurnUsername());
            });
        break;
      case SHOW_ROADS_AT_SETUP:
        Platform.runLater(
            () -> {
              gameController.settlementBuilt(finalMsg);
              if (stompUsername.equals(finalMsg.getNickname())) {
                gameController.showOptionalRoadsAtSetup();
              }
            });
        break;
      case SHOW_ROADS_AT_SETUP_AND_GATHER:
        Platform.runLater(
            () -> {
              gameController.settlementBuilt(finalMsg);
              if (stompUsername.equals(finalMsg.getNickname())) {
                gameController.showOptionalRoadsAtSetup();
                try {
                  gameController.gatherResourcesAtSetup(finalMsg.getContent());
                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }
            });
        break;
      case SKIP_SETUP_TURN:
        Platform.runLater(
            () -> {
              gameController.roadBuilt(finalMsg);
              gameController.setupHelper(finalMsg.getTurnUsername());
              gameController.highlightPlayerInfoBox(finalMsg.getTurnUsername());
            });
        break;
      case END_SETUP:
        Platform.runLater(
            () -> {
              gameController.roadBuilt(finalMsg);
              gameController.turnHelper(finalMsg.getTurnUsername());
            });
        break;
      case THROW_DICE:
        String[] diceResults = finalMsg.getContent().split("/");
        Platform.runLater(
            () ->
                gameController.addChatMessage(
                    finalMsg.getNickname() + " rolled " + diceResults[0] + " " + diceResults[1]));
        Platform.runLater(
            () -> {
              int d1 = Integer.parseInt(diceResults[0]), d2 = Integer.parseInt(diceResults[1]);
              try {
                gameController.gatherNewResources(d1 + d2);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              gameController.diceThrowAnimation(finalMsg.getTurnUsername(), d1, d2);
            });
        break;
      case RESOURCE_CHANGE:
        Platform.runLater(
            () -> {
              gameController.setPlayerResourceInfoPanel(
                  finalMsg.getNickname(), finalMsg.getContent());
              gameController.setPlayerScorePanel(finalMsg.getNickname(), finalMsg.getContent());
            });
        break;
      case SKIP_TURN:
        Platform.runLater(
            () -> {
              gameController.turnHelper(finalMsg.getTurnUsername());
              gameController.highlightPlayerInfoBox(finalMsg.getTurnUsername());
            });
        break;
      case BUILD_SETTLEMENT:
        Platform.runLater(
            () -> {
              gameController.addChatMessage(finalMsg.getNickname() + " built a settlement!");
              gameController.settlementBuilt(finalMsg);
            });
        break;
      case BUILD_ROAD:
        Platform.runLater(
            () -> {
              gameController.addChatMessage(finalMsg.getNickname() + " built a road!");
              gameController.roadBuilt(finalMsg);
            });
        break;
      case UPGRADE_SETTLEMENT:
        Platform.runLater(
            () -> {
              gameController.addChatMessage(
                  finalMsg.getNickname() + " upgraded a settlement to a city!");
              gameController.settlementUpgraded(finalMsg);
            });
        break;
      case TRADE_OFFER_RECEIVED:
        Platform.runLater(() -> gameController.showTradeOffer(finalMsg));
        break;
      case TRADE_OFFER_ACCEPTED:
        Platform.runLater(() -> gameController.tradeOfferAccepted(finalMsg));
        break;
      default:
        break;
    }
    LOG.info("{} Received message: {}", stompUsername, msg);
  }

  @Override
  public void handleException(
      StompSession session,
      StompCommand command,
      StompHeaders headers,
      byte[] payload,
      Throwable exception) {
    LOG.info("Exception in stomp session: ", exception);

    Platform.runLater(() -> connected.set(false));
  }

  @Override
  public void handleTransportError(StompSession session, Throwable exception) {
    LOG.error("Retrieved a transport error: ", exception);

    Platform.runLater(() -> connected.set(false));
  }

  @Override
  public Type getPayloadType(StompHeaders headers) {
    return String.class;
  }

  public void disconnect() {
    if (stompSession != null) {
      LOG.info("Disconnecting from  stomp session {}", stompSession.getSessionId());
      stompSession.disconnect();
    }

    Platform.runLater(() -> connected.set(false));
  }

  public void sendChatMessage(Message message) throws JsonProcessingException {
    if (!message.getContent().trim().isEmpty()) {
      stompSession.send("/app/chat", objectMapper.writeValueAsString(message));
    }
  }

  public void sendCommand(Message message) throws JsonProcessingException {
    stompSession.send("/app/room", objectMapper.writeValueAsString(message));
  }
}
