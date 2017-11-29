package com.paul;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHandler {

  public byte[] hash(byte[] bytes) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      md.reset();
      md.update(bytes);
      return md.digest();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return null;
  }
}
