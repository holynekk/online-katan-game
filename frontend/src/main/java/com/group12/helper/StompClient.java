package com.group12.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StompClient implements StompSessionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(StompClient.class);

  private static final String ENDPOINT_URL = "ws://localhost:8080/ws";
  private StompSession stompSession;

  private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(false);
  private final ObservableList<Message> greetings = FXCollections.observableArrayList();

  private final ObjectMapper objectMapper;

  public StompClient() {
    this.objectMapper = new ObjectMapper();
  }

  public ObservableList<Message> getGreetings() {
    return greetings;
  }

  public ReadOnlyBooleanProperty connectedProperty() {
    return connected.getReadOnlyProperty();
  }

  public void connect() {
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(webSocketClient));
    SockJsClient sockJsClient = new SockJsClient(transports);
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.afterPropertiesSet();
    WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
    stompClient.setMessageConverter(new StringMessageConverter());
    stompClient.setTaskScheduler(taskScheduler);
    stompClient.setDefaultHeartbeat(new long[] {0, 0});

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

    subscribe("/topic/greetings");

    Platform.runLater(() -> connected.set(true));
  }

  public void subscribe(String topic) {
    LOG.info("Subscribing to topic {} for session {}", topic, stompSession.getSessionId());
    stompSession.subscribe(topic, this);
  }

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    Message greeting = (Message) payload;
    LOG.info("Received message: {}", greeting.getContent());
    Platform.runLater(() -> greetings.add(greeting));
  }

  @Override
  public void handleException(
      StompSession session,
      StompCommand command,
      StompHeaders headers,
      byte[] payload,
      Throwable exception) {
    LOG.info("Exception in stomp session", exception);

    Platform.runLater(() -> connected.set(false));
  }

  @Override
  public void handleTransportError(StompSession session, Throwable exception) {
    LOG.error("Retrieved a transport error:", exception);

    Platform.runLater(() -> connected.set(false));
  }

  @Override
  public Type getPayloadType(StompHeaders headers) {
    return Message.class;
  }

  public void disconnect() {
    if (stompSession != null) {
      LOG.info("Disconnecting from  stomp session {}", stompSession.getSessionId());
      stompSession.disconnect();
    }

    Platform.runLater(() -> connected.set(false));
  }

  public void send(Message helloRequest) throws JsonProcessingException {
    stompSession.send("/app/hello", objectMapper.writeValueAsString(helloRequest));
  }
}
