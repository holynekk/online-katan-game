package com.group12.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity class representing a score record. This class holds information about a user's all
 * recorded scores and the date it is recorded. Has an OnetoOne relationship with User class.
 */
@Entity
@Table(name = "game_history")
public class GameHistory {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private int gameId;

    private int totalScore;

    private int didWon;

    private LocalDateTime history;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "game_id")
    private Game game;

    public GameHistory(int totalScore, int didWon, LocalDateTime history, User user, Game game) {
        this.totalScore = totalScore;
        this.didWon = didWon;
        this.history = history;
        this.user = user;
        this.game = game;
    }

    public int getId() {
        return gameId;
    }

    public void setId(int id) {
        this.gameId = id;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int isDidWon() {
        return didWon;
    }

    public void setDidWon(int didWon) {
        this.didWon = didWon;
    }

    public LocalDateTime getHistory() {
        return history;
    }

    public void setHistory(LocalDateTime history) {
        this.history = history;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
