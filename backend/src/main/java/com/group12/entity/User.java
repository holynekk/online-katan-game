package com.group12.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "katan_user")
public class User {
  @Id
  @Column(name = "user_id")
  private int userId;

  private String username;

  private String firstName;

  private String lastName;

  private String passwordHash;

  private String salt;

  private String displayName;

  private String email;

  private LocalDateTime lastPasswordUpdate;

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getLastPasswordUpdate() {
    return lastPasswordUpdate;
  }

  public void setLastPasswordUpdate(LocalDateTime lastPasswordUpdate) {
    this.lastPasswordUpdate = lastPasswordUpdate;
  }
}
