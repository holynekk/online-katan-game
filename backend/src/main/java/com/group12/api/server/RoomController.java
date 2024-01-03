package com.group12.api.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.entity.chat.Message;
import com.group12.entity.chat.MessageType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

import static com.group12.util.GameUtil.*;

@Controller
public class RoomController {

  private String turnUsername;
  private int turnCount;

  private List<String> playerList;
  private List<String> readyPlayerList;

  private final ObjectMapper objectMapper;

  public RoomController() {
    this.turnUsername = "";
    this.turnCount = 0;
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
        msg.setUserColor(playerColors.get(this.playerList.size() - 1));
        break;
      case START_GAME:
        turnUsername = playerList.get(0);
        msg.setContent(shuffleBoardTiles());
        msg.setTurnUsername(this.turnUsername);
        break;
      case READY:
        readyPlayerList.add(msg.getNickname());
        msg.setContent(Boolean.toString(this.readyPlayerList.size() == 2));
        break;
      case THROW_DICE:
        msg.setContent(throwDice());
        msg.setTurnUsername(this.playerList.get(turnCount % this.playerList.size()));
        break;
      case RESOURCE_CHANGE:
        break;
      case SKIP_TURN:
        msg.setTurnUsername(this.playerList.get(++turnCount % this.playerList.size()));
        break;
      case BUILD_SETTLEMENT:
        if (this.turnCount < this.playerList.size()) {
          msg.setMsgType(MessageType.SHOW_ROADS_AT_SETUP);
          break;
        } else if (this.turnCount < this.playerList.size() * 2) {
          msg.setMsgType(MessageType.SHOW_ROADS_AT_SETUP_AND_GATHER);
          break;
        }
        break;
      case BUILD_ROAD:
        if (this.turnCount < this.playerList.size() - 1) {
          msg.setMsgType(MessageType.SKIP_SETUP_TURN);
          msg.setTurnUsername(this.playerList.get(++turnCount % this.playerList.size()));
        } else if (this.turnCount < (this.playerList.size() * 2 - 1)) {
          msg.setMsgType(MessageType.SKIP_SETUP_TURN);
          msg.setTurnUsername(
              this.playerList.get(
                  this.playerList.size() - 1 - (++turnCount % this.playerList.size())));
        } else if (this.turnCount < (this.playerList.size() * 2)) {
          this.turnCount++;
          msg.setMsgType(MessageType.END_SETUP);
          msg.setTurnUsername(this.playerList.get(0));
        }
        break;
      case TRADE_OFFER_SENT:
        msg.setMsgType(MessageType.TRADE_OFFER_RECEIVED);
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
    msg.setContent(msg.getContent());
    return objectMapper.writeValueAsString(msg);
  }

  @MessageMapping("/hello")
  @SendTo("/topic/room")
  public void greeting(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
  }
}
