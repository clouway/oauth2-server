package com.clouway.oauth2.codechallenge;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class AuthorizationCodeVerifier implements CodeVerifier {
  @Override
  public boolean verify(CodeChallenge codeChallenge, String providedCodeVerifier) {
    try {
      //no code verifier was provided and no code challenge was saved so this is a normal OAuth2 code flow
      if (providedCodeVerifier.isEmpty() && !codeChallenge.isProvided()) {
        return true;
      }
      //there is a saved code challenge but provided providedCodeVerifier is empty.
      if (providedCodeVerifier.isEmpty() && codeChallenge.isProvided()) {
        return false;
      }
      //providedCodeVerifier was provided but no codeChallenge was saved.
      if (!providedCodeVerifier.isEmpty() && !codeChallenge.isProvided()) {
        return false;
      }

      if (codeChallenge.method.equals("plain")) {
        return codeChallenge.transformedCodeChallenge.equals(providedCodeVerifier);
      }

      if (codeChallenge.method.equals("S256")) {
        byte[] hashed = MessageDigest.getInstance("SHA-256").digest(providedCodeVerifier.getBytes(StandardCharsets.UTF_8));
        String transformedCodeVerifier = BaseEncoding.base64Url().encode(hashed);
        return transformedCodeVerifier.equals(codeChallenge.transformedCodeChallenge);
      }
      return false;
    } catch (NoSuchAlgorithmException e) {
      return false;
    }
  }
}
