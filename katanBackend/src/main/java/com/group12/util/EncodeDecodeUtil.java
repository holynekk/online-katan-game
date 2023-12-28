package com.group12.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.util.UriUtils;

public class EncodeDecodeUtil {

  public static String encodeBase64(String original) {
    return Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
  }

  public static String decodeBase64(String encoded) {
    return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
  }

  public static String encodeUrl(String original) {
    return UriUtils.encode(original, StandardCharsets.UTF_8);
  }

  public static String decodeUrl(String encoded) {
    return UriUtils.decode(encoded, StandardCharsets.UTF_8);
  }
}
