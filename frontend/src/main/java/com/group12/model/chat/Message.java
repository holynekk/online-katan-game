package com.group12.model.chat;

public class Message {
  private static final long serialVersionUID = 1L;

  private MessageType msgType;
  private String timestamp;
  private String nickname;
  private String turnUsername;
  private String userColor;
  private Boolean atSetup;
  private String userColorList;
  private String userReadyList;
  private String content;

  public Message() {}

  public Message(String type, String timestamp, String nickname, String content) {
    this.msgType = MessageType.valueOf(type);
    this.timestamp = timestamp;
    this.nickname = nickname;
    this.content = content;
  }

  public Message(MessageType type, String timestamp, String nickname, String content) {
    this.msgType = type;
    this.timestamp = timestamp;
    this.nickname = nickname;
    this.content = content;
  }

  public MessageType getMsgType() {
    return msgType;
  }

  public void setMsgType(MessageType msgType) {
    this.msgType = msgType;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getTurnUsername() {
    return turnUsername;
  }

  public void setTurnUsername(String turnUsername) {
    this.turnUsername = turnUsername;
  }

  public String getUserColor() {
    return userColor;
  }

  public void setUserColor(String userColor) {
    this.userColor = userColor;
  }

  public Boolean getAtSetup() {
    return atSetup;
  }

  public void setAtSetup(Boolean atSetup) {
    this.atSetup = atSetup;
  }

  public String getUserColorList() {
    return userColorList;
  }

  public void setUserColorList(String userColorList) {
    this.userColorList = userColorList;
  }

  public String getUserReadyList() {
    return userReadyList;
  }

  public void setUserReadyList(String userReadyList) {
    this.userReadyList = userReadyList;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Message{"
        + "msgType="
        + msgType
        + ", timestamp='"
        + timestamp
        + '\''
        + ", nickname='"
        + nickname
        + '\''
        + ", userColor='"
        + userColor
        + '\''
        + ", content='"
        + content
        + '\''
        + '}';
  }
}
