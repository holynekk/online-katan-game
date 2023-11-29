package com.group12.api.response;

import java.time.LocalDateTime;

public class GameHistoryResponse {

    private int gameId;

    private LocalDateTime history;

    private boolean didWon;

    private int score;

    public GameHistoryResponse(int gameId, LocalDateTime history, boolean didWon, int score) {
        this.gameId = gameId;
        this.history = history;
        this.didWon = didWon;
        this.score = score;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getHistory() {
        return history;
    }

    public void setHistory(LocalDateTime history) {
        this.history = history;
    }

    public boolean isDidWon() {
        return didWon;
    }

    public void setDidWon(boolean didWon) {
        this.didWon = didWon;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
