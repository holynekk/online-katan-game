package com.group12.entity.chat;

public enum MessageType {
    CONNECT,
    CONNECT_OK,
    CONNECT_FAILED,
    USER_JOINED,
    USER_LIST,
    READY,
    DISCONNECT,
    KICK,
    BAN,
    LOBBY_CHAT,
    IN_GAME_CHAT,
    START_GAME,
    SHOW_ROADS_AT_SETUP,
    SHOW_ROADS_AT_SETUP_AND_GATHER,
    THROW_DICE,
    RESOURCE_CHANGE,
    SKIP_TURN,
    SKIP_SETUP_TURN,
    END_SETUP,
    BUILD_ROAD,
    BUILD_SETTLEMENT,
    UPGRADE_SETTLEMENT,
    TRADE_OFFER_SENT,
    TRADE_OFFER_RECEIVED,
    TRADE_OFFER_ACCEPTED
}
