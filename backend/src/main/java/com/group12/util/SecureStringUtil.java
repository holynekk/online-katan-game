package com.group12.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureStringUtil {

  private static SecureRandom SECURE_RANDOM;

  private static final String STRING_SEED =
      "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  static {
    try {
      SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static String randomString(int length) {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < length; i++) {
      str.append(STRING_SEED.charAt(SECURE_RANDOM.nextInt(STRING_SEED.length())));
    }
    return str.toString();
  }
}
