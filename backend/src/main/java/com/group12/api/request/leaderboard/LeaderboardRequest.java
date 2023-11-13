package com.group12.api.request.leaderboard;

/**
 * Represents a request to get the leaderboard. This class includes the parameters for time
 * interval, pagination, and offset for leaderboard queries.
 */
public class LeaderboardRequest {

  private String timeInterval;

  private int pagination;

  private int offset;

  public String getTimeInterval() {
    return timeInterval;
  }

  public void setTimeInterval(String timeInterval) {
    this.timeInterval = timeInterval;
  }

  public int getPagination() {
    return pagination;
  }

  public void setPagination(int pagination) {
    this.pagination = pagination;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
}
