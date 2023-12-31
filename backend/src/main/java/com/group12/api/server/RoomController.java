package com.group12.api.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.entity.chat.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

import static com.group12.util.GameUtil.shuffleBoardTiles;
import static com.group12.util.GameUtil.throwDice;

@Controller
public class RoomController {

  private int turn;

  private List<String> playerList;

  private List<String> readyPlayerList;

  private final ObjectMapper objectMapper;

  public RoomController() {
    this.turn = 0;
    this.objectMapper = new ObjectMapper();
    this.playerList = new ArrayList<>();
    this.readyPlayerList = new ArrayList<>();
  }

  @MessageMapping("/room")
  @SendTo("/topic/room")
  public String room(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
    switch (msg.getMsgType()) {
      case USER_JOINED:
        playerList.add(msg.getNickname());
        msg.setContent(StringUtils.join(this.playerList, "/"));
        break;
      case START_GAME:
        msg.setContent(shuffleBoardTiles());
        break;
      case READY:
        readyPlayerList.add(msg.getNickname());
        msg.setContent(Boolean.toString(this.readyPlayerList.size() == 2));
        break;
      case THROW_DICE:
        msg.setContent(throwDice());
        break;
      default:
        break;
    }

    return objectMapper.writeValueAsString(msg);
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
