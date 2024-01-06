package com.group12.api.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.entity.OnlineGame;
import com.group12.entity.chat.Message;
import com.group12.entity.chat.MessageType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.group12.util.GameUtil.*;

@Controller
public class RoomController {

  private final ObjectMapper objectMapper;

  HashMap<String, OnlineGame> gameList = new HashMap<>();

  public RoomController() {
    this.objectMapper = new ObjectMapper();
  }

  @MessageMapping("/room/{gameId}")
  @SendTo("/topic/room/{gameId}")
  public String room(@DestinationVariable String gameId, String message)
      throws JsonProcessingException {
    Message msg = objectMapper.readValue(message, Message.class);
    OnlineGame onlineGame = gameList.get(gameId);
    switch (msg.getMsgType()) {
      case GAME_CREATED:
        OnlineGame newOnlineGame = new OnlineGame();
        gameList.put(gameId, newOnlineGame);
        break;
      case USER_JOINED:
        String clr = onlineGame.getNewColor();
        onlineGame.addPlayer(msg.getNickname());
        onlineGame.addUserColor(clr);
        msg.setContent(StringUtils.join(onlineGame.getPlayerList(), "/"));
        msg.setUserColorList(StringUtils.join(onlineGame.getUserColorList(), "/"));
        msg.setUserReadyList(StringUtils.join(onlineGame.getUserReadyList(), "/"));
        gameList.put(gameId, onlineGame);
        break;
      case LEAVE:
        onlineGame.removePlayer(msg.getNickname());
        onlineGame.removeUserColor(msg.getUserColor());
        onlineGame.removeUserReady(msg.getNickname());
        onlineGame.addPlayerColors(msg.getUserColor());
        msg.setContent(StringUtils.join(onlineGame.getPlayerList(), "/"));
        msg.setUserColorList(StringUtils.join(onlineGame.getUserColorList(), "/"));
        msg.setUserReadyList(StringUtils.join(onlineGame.getUserReadyList(), "/"));
        msg.setMsgType(MessageType.USER_LEFT);
        gameList.put(gameId, onlineGame);
        break;
      case KICK:
        msg.setMsgType(MessageType.USER_KICKED);
        break;
      case START_GAME:
        String turnUsername = onlineGame.getFirstTurnUsername();
        msg.setContent(shuffleBoardTiles());
        msg.setTurnUsername(turnUsername);
        gameList.put(gameId, onlineGame);
        break;
      case READY:
        onlineGame.addUserReady(msg.getNickname());
        msg.setContent(
            Boolean.toString(
                onlineGame.getUserReadyList().size() >= 2
                    && onlineGame.getUserReadyList().size() <= 4
                    && onlineGame.getUserReadyList().size() <= onlineGame.getPlayerList().size()));
        gameList.put(gameId, onlineGame);
        break;
      case THROW_DICE:
        msg.setContent(throwDice());
        msg.setTurnUsername(onlineGame.getTurnUsername());
        gameList.put(gameId, onlineGame);
        break;
      case SKIP_TURN:
        msg.setTurnUsername(onlineGame.getSkipTurnUsername());
        break;
      case BUILD_SETTLEMENT:
        if (onlineGame.getTurnCount() < onlineGame.getPlayerList().size()) {
          msg.setMsgType(MessageType.SHOW_ROADS_AT_SETUP);
        } else if (onlineGame.getTurnCount() < onlineGame.getPlayerList().size() * 2) {
          msg.setMsgType(MessageType.SHOW_ROADS_AT_SETUP_AND_GATHER);
        }
        break;
      case BUILD_ROAD:
        if (onlineGame.getTurnCount() < onlineGame.getPlayerList().size() - 1) {
          msg.setMsgType(MessageType.SKIP_SETUP_TURN);
          msg.setAtSetup(true);
          msg.setTurnUsername(onlineGame.getSkipTurnUsername());
        } else if (onlineGame.getTurnCount() < (onlineGame.getPlayerList().size() * 2 - 1)) {
          msg.setMsgType(MessageType.SKIP_SETUP_TURN);
          msg.setAtSetup(true);
          msg.setTurnUsername(onlineGame.getSetupTurnUsername());
        } else if (onlineGame.getTurnCount() < (onlineGame.getPlayerList().size() * 2)) {
          onlineGame.incrementTurnCount();
          msg.setAtSetup(true);
          msg.setMsgType(MessageType.END_SETUP);
          msg.setTurnUsername(onlineGame.getFirstTurnUsername());
        } else {
          msg.setAtSetup(false);
          if (msg.getLongestRoadLength() > onlineGame.getLongestRoadLength()) {
            onlineGame.setLongestRoadUser(msg.getNickname());
            onlineGame.setLongestRoadLength(msg.getLongestRoadLength());
            msg.setUserWithLongestRoad(msg.getNickname());
          } else {
            msg.setUserWithLongestRoad(msg.getNickname());
          }
        }
        gameList.put(gameId, onlineGame);
        break;
      case TRADE_OFFER_SENT:
        msg.setMsgType(MessageType.TRADE_OFFER_RECEIVED);
        break;
      default:
        break;
    }

    return objectMapper.writeValueAsString(msg);
  }

  @MessageMapping("/chat/{gameId}")
  @SendTo("/topic/chat/{gameId}")
  public String chat(@DestinationVariable String gameId, String message)
      throws JsonProcessingException {
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
