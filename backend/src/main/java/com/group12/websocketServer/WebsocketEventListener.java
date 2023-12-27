package com.group12.websocketServer;

import com.group12.entity.chat.Message;
import com.group12.entity.chat.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebsocketEventListener {

  private static final Logger LOG = LoggerFactory.getLogger(WebsocketEventListener.class);

  @Autowired private SimpMessageSendingOperations sendingOperations;

  @EventListener
  public void handleWebSocketConnectListener(final SessionConnectedEvent event) {
    LOG.info("New connection has been established!");
  }

  //    @EventListener
  //    public void handleWebSocketDisconnectListener(final SessionConnectedEvent event) {
  //        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
  //
  //        final String username = (String) headerAccessor.getSessionAttributes().get("username");
  //
  //        final Message message =
  // Message.builder().type(MessageType.DISCONNECT).sender(username).build();
  //
  //        sendingOperations.convertAndSend("/topic/public", message);
  //    }

}
