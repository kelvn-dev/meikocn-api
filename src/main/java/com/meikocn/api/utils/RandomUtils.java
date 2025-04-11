package com.meikocn.api.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomUtils {
  public static String generateUrlSafeRandomString(int length) {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[length];
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}
