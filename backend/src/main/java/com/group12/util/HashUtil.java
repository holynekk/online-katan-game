package com.group12.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.util.encoders.Hex;

public class HashUtil {

  /**
   * Used for password encryption.
   *
   * @param original - raw string data to be used in encryption
   * @param salt - random string to empower password encryption
   * @return encrypted string
   */
  public static String bcrypt(String original, String salt) {
    return OpenBSDBCrypt.generate(
        original.getBytes(StandardCharsets.UTF_8), salt.getBytes(StandardCharsets.UTF_8), 5);
  }

  /**
   * Returns true if there is a match between encrypted values, else returns false.
   *
   * @param original - raw string data to be used in encryption
   * @param hashValue - already encrypted data
   * @return boolean value
   */
  public static boolean isBcryptMatch(String original, String hashValue) {
    return OpenBSDBCrypt.checkPassword(hashValue, original.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * sha256 encryption util function for encrypting sensitive information.
   *
   * @param original - raw string data to be encrypted
   * @param salt - random string to empower password encryption
   * @return
   * @throws Exception
   */
  public static String sha256(String original, String salt) throws Exception {
    String originalWithSalt = StringUtils.join(original, salt);
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(originalWithSalt.getBytes(StandardCharsets.UTF_8));
    return new String(Hex.encode(hash));
  }

  /**
   * Returns true if there is a match between encrypted values, else returns false.
   *
   * @param original - raw string data to be encrypted
   * @param salt - random string to empower password encryption
   * @param hashValue - already encrypted data
   * @return booleand value
   * @throws Exception - throws exception if encryption got some error.
   */
  public static boolean isSha256Match(String original, String salt, String hashValue)
      throws Exception {
    String reHashValue = sha256(original, salt);
    return StringUtils.equals(hashValue, reHashValue);
  }
}
