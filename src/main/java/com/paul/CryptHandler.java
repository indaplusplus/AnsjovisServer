package com.paul;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class CryptHandler {

  private KeyPair key;

  public CryptHandler() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(512);
      key = keyGen.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  public PublicKey getPublicKey() {
    return key.getPublic();
  }

  public byte[] encrypt(String message) {
    byte[] cipherText = null;

    try {
      final Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
      cipherText = cipher.doFinal(message.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return cipherText;
  }

  public byte[] decrypt(byte[] text) {
    byte[] decrypted = null;

    try {
      final Cipher cipher = Cipher.getInstance("RSA");

      cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
      decrypted = cipher.doFinal(text);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return decrypted;
  }
}
