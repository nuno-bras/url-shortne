package casa.bras.urlshortner.url.utils;

import casa.bras.urlshortner.common.errors.ApplicationException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;

public class UrlHashUtils {
  private static final String ALPHABET =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  private UrlHashUtils() {}

  public static String hash(URL url) {
    String longHash = longHash(url.toString());

    StringBuilder shortURL = new StringBuilder();
    for (char c : longHash.toCharArray()) {
      shortURL.append(ALPHABET.charAt(Math.abs(c) % ALPHABET.length()));
      if (shortURL.length() == 7) break;
    }

    return shortURL.toString();
  }

  private static String longHash(String url) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(url.getBytes());

      return StringUtils.strip(Base64.getUrlEncoder().encodeToString(hashBytes), "=");
    } catch (NoSuchAlgorithmException e) {
      throw new ApplicationException(e);
    }
  }
}
