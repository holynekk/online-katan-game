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
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static com.group12.util.GameUtil.*;

@Controller
public class RoomController {
  private String turnUsername;
  private int turnCount;
  private int longestRoadLength;
  private String longestRoadUser;

  private List<String> playerList;
  private List<String> userColorList;
  private List<String> userReadyList;
  private Stack<String> playerColors;

  private final ObjectMapper objectMapper;

  public RoomController() {
    this.turnUsername = "";
    this.turnCount = 0;
    this.longestRoadLength = -1;
    this.longestRoadUser = "";
    this.objectMapper = new ObjectMapper();
    this.playerList = new ArrayList<>();
    this.userColorList = new ArrayList<>();
    this.userReadyList = new ArrayList<>();
    playerColors = new Stack<>();
    playerColors.addAll(Arrays.asList("pink", "purple", "blue", "green", "orange", "red"));
  }

  @MessageMapping("/room")
  @SendTo("/topic/room")
  public String room(String message) throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
    switch (msg.getMsgType()) {
      case USER_JOINED:
        String clr = playerColors.pop();
        playerList.add(msg.getNickname());
        userColorList.add(clr);
        msg.setContent(StringUtils.join(this.playerList, "/"));
        msg.setUserColorList(StringUtils.join(this.userColorList, "/"));
        msg.setUserReadyList(StringUtils.join(this.userReadyList, "/"));
        break;
      case LEAVE:
        playerList.remove(msg.getNickname());
        userColorList.remove(msg.getUserColor());
        userReadyList.remove(msg.getNickname());
        playerColors.add(msg.getUserColor());
        msg.setContent(StringUtils.join(this.playerList, "/"));
        msg.setUserColorList(StringUtils.join(this.userColorList, "/"));
        msg.setUserReadyList(StringUtils.join(this.userReadyList, "/"));
        msg.setMsgType(MessageType.USER_LEFT);
        break;
      case KICK:
        msg.setMsgType(MessageType.USER_KICKED);
        break;
      case START_GAME:
        turnUsername = playerList.get(0);
        msg.setContent(shuffleBoardTiles());
        msg.setTurnUsername(this.turnUsername);
        break;
      case READY:
        userReadyList.add(msg.getNickname());
        msg.setContent(Boolean.toString(this.userReadyList.size() == 2));
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
          msg.setAtSetup(true);
          msg.setTurnUsername(this.playerList.get(++turnCount % this.playerList.size()));
        } else if (this.turnCount < (this.playerList.size() * 2 - 1)) {
          msg.setMsgType(MessageType.SKIP_SETUP_TURN);
          msg.setAtSetup(true);
          msg.setTurnUsername(
              this.playerList.get(
                  this.playerList.size() - 1 - (++turnCount % this.playerList.size())));
        } else if (this.turnCount < (this.playerList.size() * 2)) {
          this.turnCount++;
          msg.setAtSetup(true);
          msg.setMsgType(MessageType.END_SETUP);
          msg.setTurnUsername(this.playerList.get(0));
        } else {
          msg.setAtSetup(false);
          if (msg.getLongestRoadLength() > longestRoadLength) {
            longestRoadUser = msg.getNickname();
            longestRoadLength = msg.getLongestRoadLength();
            msg.setUserWithLongestRoad(longestRoadUser);
          } else {
            msg.setUserWithLongestRoad(longestRoadUser);
          }
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
