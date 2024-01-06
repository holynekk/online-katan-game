package com.group12.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.controller.OnlineGameController;
import com.group12.controller.RoomController;
import com.group12.model.chat.Message;
import com.group12.model.chat.MessageType;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
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

import static com.group12.helper.HttpClientHelper.getSessionCookie;

public class StompClient implements StompSessionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(StompClient.class);

  private static final String ENDPOINT_URL = "ws://localhost:8080/ws";

  private static StompSession stompSession;
  public static WebSocketStompClient stompClient;
  private String stompUsername;
  private String gameId;

  private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(false);

  private final ObjectMapper objectMapper;
  private final RoomController roomController;
  private OnlineGameController gameController;

  public StompClient(RoomController roomController, String stompUsername, String gameId) {
    this.objectMapper = new ObjectMapper();
    this.roomController = roomController;
    this.stompUsername = stompUsername;
    this.gameId = gameId;

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

  /** A method to connect a websocket with the predefined variables. */
  public void connect() {
    try {
      stompSession = stompClient.connectAsync(ENDPOINT_URL, this).get();
    } catch (Exception e) {
      LOG.error("Connection failed.", e);
    }
  }

/**
* A method to call after websocket connection has been established. Allows client to subscribe to some specific websocket topics.
 * @param session  - Current session instance.
 * @param connectedHeaders - Stomp headers
*/
  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    LOG.info(
        " Connection to STOMP server established Session: {} Headers: {}",
        session.getSessionId(),
        connectedHeaders);
    subscribe(String.format("/topic/room/%s", this.gameId));
    subscribe(String.format("/topic/chat/%s", this.gameId));

    Platform.runLater(() -> connected.set(true));
  }

  /**
   * A method to subscribe to a specific topic with the provided topic path.
   *
   * @param topic - Topic path.
   */
  public void subscribe(String topic) {
    LOG.info("Subscribing to topic {} for session {}", topic, stompSession.getSessionId());
    stompSession.subscribe(topic, this);
  }

  /**
   * A method to process messages which comes from the websocket server. All game commands and chat
   * messages are processed here and the relevant helper methods in controllers are called.
   *
   * @param headers - Stomp headers.
   * @param payload - Message payload that comes from the websocket server.
   */
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
      case GAME_CREATED:
        Platform.runLater(roomController::gameCreated);
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
      case USER_LEFT:
        Platform.runLater(
            () -> {
              roomController.refreshPlayerList(finalMsg);
              roomController.addChatMessage(finalMsg.getNickname() + " has left the room!");
              roomController.leftRoom(finalMsg);
            });
        break;
      case USER_KICKED:
        Platform.runLater(
            () -> {
              roomController.addChatMessage(finalMsg.getContent() + " got kicked!");
              roomController.userKicked(finalMsg);
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
      case GAME_ENDED:
        Platform.runLater(() -> gameController.gameEnded(finalMsg));
        break;
      default:
        break;
    }
    LOG.info("{} Received message: {}", stompUsername, msg);
  }

  /**
   * A method to handle exceptions.
   *
   * @param session - Current session instance.
   * @param command - Stomp command.
   * @param headers - Stomp headers.
   * @param payload - Exception payload.
   * @param exception - Exception that will be thrown.
   */
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

  /**
   * A method to handle message transport errors.
   *
   * @param session - Current session instance.
   * @param exception - Exception that will be thrown.
   */
  @Override
  public void handleTransportError(StompSession session, Throwable exception) {
    LOG.error("Retrieved a transport error: ", exception);

    Platform.runLater(() -> connected.set(false));
  }

  /**
   * A method to get type of messages.
   *
   * @param headers - Stomp headers
   * @return - The type of messages
   */
  @Override
  public Type getPayloadType(StompHeaders headers) {
    return String.class;
  }

  /** A method to terminate the current session between client and the websocket server. */
  public void disconnect() {
    if (stompSession != null) {
      LOG.info("Disconnecting from stomp session {}", stompSession.getSessionId());
      stompSession.disconnect();
    }

    Platform.runLater(() -> connected.set(false));
  }

  /**
   * A method to exit a game successfully while in game or lobby.
   *
   * @param gameId - Game's id to redirect exit game message/command to a specific websocket topic.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  public static void exitGame(String gameId) throws JsonProcessingException {
    if (stompSession != null) {
      ObjectMapper objectMapper = new ObjectMapper();
      Message msg =
          new Message(MessageType.LEAVE, "Now", getSessionCookie("username"), "Yo I'm leaving!");
      stompSession.send(
          String.format("/app/room/%s", gameId.isEmpty() ? "none" : gameId),
          objectMapper.writeValueAsString(msg));
      stompSession.disconnect();
    }
  }

  /**
   * A method to send chat messages to a specific chat topic with a room/game id.
   *
   * @param message - Message object that will be sent to websocket server.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  public void sendChatMessage(Message message) throws JsonProcessingException {
    if (!message.getContent().trim().isEmpty()) {
      stompSession.send(
          String.format("/app/chat/%s", this.gameId), objectMapper.writeValueAsString(message));
    }
  }

  /**
   * A method to send game commands to a specific room topic with a room/game id.
   *
   * @param message - Message object that will be sent to websocket server.
   * @throws JsonProcessingException - exception of json serialize/deserialize function.
   */
  public void sendCommand(Message message) throws JsonProcessingException {
    stompSession.send(
        String.format("/app/room/%s", this.gameId), objectMapper.writeValueAsString(message));
  }
}
