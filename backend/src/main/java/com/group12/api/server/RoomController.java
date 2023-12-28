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

  @MessageMapping("/join")
  @SendTo("/topic/room")
  public void join(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
    System.out.println(msg);
  }

  @MessageMapping("/chat")
  @SendTo("/topic/chat")
  public String chat(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
    msg.setContent(msg.getNickname() + ": " + msg.getContent());
    return objectMapper.writeValueAsString(msg);
  }

  @MessageMapping("/hello")
  @SendTo("/topic/room")
  public void greeting(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
  }
}
