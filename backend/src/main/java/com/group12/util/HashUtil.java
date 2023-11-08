package com.group12.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.util.encoders.Hex;

public class HashUtil {

  public static String bcrypt(String original, String salt) {
    return OpenBSDBCrypt.generate(
        original.getBytes(StandardCharsets.UTF_8), salt.getBytes(StandardCharsets.UTF_8), 5);
  }

  public static boolean isBcryptMatch(String original, String hashValue) {
    return OpenBSDBCrypt.checkPassword(hashValue, original.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha256(String original, String salt) throws Exception {
    String originalWithSalt = StringUtils.join(original, salt);
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(originalWithSalt.getBytes(StandardCharsets.UTF_8));
    return new String(Hex.encode(hash));
  }

  public static boolean isSha256Match(String original, String salt, String hashValue)
      throws Exception {
    String reHashValue = sha256(original, salt);
    return StringUtils.equals(hashValue, reHashValue);
  }
}
