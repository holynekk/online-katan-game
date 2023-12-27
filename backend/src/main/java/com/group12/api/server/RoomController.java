package com.group12.api.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.entity.chat.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class RoomController {

  private final ObjectMapper objectMapper;

  public RoomController() {
    objectMapper = new ObjectMapper();
  }

  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public void greeting(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
  }
}
