package com.group12.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Security;

public class EncryptDecryptUtil {
  private static final String ALGORITHM = "AES";
  private static final String AES_TRANSFORMATION = "AES/CBC/PKCS7Padding";
  private static final String IV = "LqV4g8gT1DqjSPB6";

  private static IvParameterSpec paramSpec;
  private static Cipher cipher;

  static {
    Security.addProvider(new BouncyCastleProvider());
    paramSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
    try {
      cipher = Cipher.getInstance(AES_TRANSFORMATION);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String encryptAes(String original, String secretKey)
      throws IllegalBlockSizeException,
          BadPaddingException,
          InvalidAlgorithmParameterException,
          InvalidKeyException {
    final SecretKeySpec secretKeySpec =
        new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);

    byte[] resultBytes = cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));

    return new String(Hex.encode(resultBytes));
  }

  public static String decryptAes(String encryted, String secretKey)
      throws InvalidAlgorithmParameterException,
          InvalidKeyException,
          IllegalBlockSizeException,
          BadPaddingException {
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec);

    byte[] encryptedBytes = Hex.decode(encryted);
    byte[] resultBytes = cipher.doFinal(encryptedBytes);

    return new String(resultBytes);
  }
}
