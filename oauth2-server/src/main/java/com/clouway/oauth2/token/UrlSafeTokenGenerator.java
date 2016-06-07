package com.clouway.oauth2.token;

import com.google.common.io.BaseEncoding;

import java.util.UUID;

/**
 * UrlSafeTokenGenerator is a token generator which is encoding randomly generated tokens in Base64Url format to ensure
 * that the generated tokens could be passed as URL parameters.
 * <p/>
 * It also removes the right padded '=' character, to prevent parameter collision.
 *
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class UrlSafeTokenGenerator implements TokenGenerator {

  /**
   * Generates a new URL safe token.
   *
   * @return the newly generated token.
   */
  @Override
  public String generate() {
    String generatedToken = BaseEncoding.base64Url().encode(UUID.randomUUID().toString().getBytes());
    return trimRight(generatedToken, '=');
  }

  /**
   * Trim returns string with all leading and trailing Unicode code points contained in cutset removed.
   */
  private String trimRight(String text, char cutset) {
    int i = text.length() - 1;
    while (i >= 0 && cutset == text.charAt(i)) {
      i--;
    }
    return text.substring(0, i + 1);
  }
}