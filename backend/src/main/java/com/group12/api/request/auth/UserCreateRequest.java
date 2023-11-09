package com.group12.api.request.auth;

public class UserCreateRequest {

  private String username;

  private String password;

  private String displayName;

  private String email;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  @Override
  public String toString() {
    return "UserCreateRequest{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", displayName='" + displayName + '\'' +
            ", email='" + email + '\'' +
            '}';
  }
}
