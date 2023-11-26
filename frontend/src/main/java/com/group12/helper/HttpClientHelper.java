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

  public HttpClientHelper() throws URISyntaxException {
    cookieManager = new CookieManager();
    httpClient =
        HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .connectTimeout(Duration.ofSeconds(1800))
            .build();
  }

  public static HttpClient getClient() {
    return httpClient;
  }

  public static void addNewSessionCookie(String cookieHeader, String cookieVal) {
    cookieManager.getCookieStore().add(null, new HttpCookie(cookieHeader, cookieVal));
  }

  public static void removeAllSessionCookies() {
    cookieManager.getCookieStore().removeAll();
  }

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

  public static List<HttpCookie> getAllSessionCookies() {
    return cookieManager.getCookieStore().getCookies();
  }
}
