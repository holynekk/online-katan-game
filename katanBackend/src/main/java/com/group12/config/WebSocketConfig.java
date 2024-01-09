package com.group12.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * A method to register domain of the websocket server.
   *
   * @param registry - Stomp endpoint registry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setHandshakeHandler(new DefaultHandshakeHandler())
        .setAllowedOrigins("*")
        .withSockJS();
  }

  /**
   * A method to configure root of the websocket topics' root paths.
   *
   * @param registry - Message broker registry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/topic");
  }
}
