package com.group12.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int totalScore;

  private LocalDateTime history;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(int totalScore) {
    this.totalScore = totalScore;
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
}
