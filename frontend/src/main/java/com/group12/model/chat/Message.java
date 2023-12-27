package com.group12.model.chat;

public class Message {
  private static final long serialVersionUID = 1L;

  private MessageType msgType;
  private String timestamp;
  private String nickname;
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
        + ", content='"
        + content
        + '\''
        + '}';
  }
}
