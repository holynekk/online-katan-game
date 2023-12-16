package com.group12.helper;

import org.springframework.stereotype.Component;

import java.net.*;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class HttpClientHelper {
  private static HttpClient httpClient;
  private static CookieManager cookieManager;

  /** Constructor to create a global http-client with its cookie handler. */
  public HttpClientHelper() {
    cookieManager = new CookieManager();
    httpClient =
        HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .connectTimeout(Duration.ofSeconds(1800))
            .build();
  }

  /**
   * Client getter method.
   *
   * @return - global http-client
   */
  public static HttpClient getClient() {
    return httpClient;
  }

  /**
   * Method to save a cookie for the current http-client in its cookie store.
   *
   * @param cookieHeader - Cookie key to store.
   * @param cookieVal - Cookie value corresponds to a key.
   */
  public static void addNewSessionCookie(String cookieHeader, String cookieVal) {
    cookieManager.getCookieStore().add(null, new HttpCookie(cookieHeader, cookieVal));
  }

  /** Method to remove all session cookies for the http-client. */
  public static void removeAllSessionCookies() {
    cookieManager.getCookieStore().removeAll();
  }

  /**
   * Method to fetch a specific cookie with a key.
   *
   * @param cookieHeader - Provided cookie header (key).
   * @return - A specific cookie value corresponds to the cookieHeader.
   */
  public static String getSessionCookie(String cookieHeader) {
    List<HttpCookie> sessionCookies = cookieManager.getCookieStore().getCookies();
    String result = "";
    for (HttpCookie cookie : sessionCookies) {
      if (cookie.getName().equals(cookieHeader)) {
        result = cookie.getValue();
      }
    }
    return result;
  }

  /**
   * Method to provide all session cookies.
   *
   * @return - List of all session cookies
   */
  public static List<HttpCookie> getAllSessionCookies() {
    return cookieManager.getCookieStore().getCookies();
  }
}
