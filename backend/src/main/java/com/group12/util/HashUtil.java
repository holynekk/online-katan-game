package com.group12.util;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;


public class HashUtil {

  public static String bcrypt(String original, String salt) {
    return OpenBSDBCrypt.generate(
        original.getBytes(StandardCharsets.UTF_8), salt.getBytes(StandardCharsets.UTF_8), 5);
  }

  public static boolean isBcryptMatch(String original, String hashValue) {
    return OpenBSDBCrypt.checkPassword(hashValue, original.getBytes(StandardCharsets.UTF_8));
  }
}
