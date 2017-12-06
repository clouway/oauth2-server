package com.clouway.oauth2.codechallenge;

import com.google.common.io.BaseEncoding;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class AuthorizationCodeVerifierTest {

  private CodeVerifier codeVerifier = new AuthorizationCodeVerifier();

  @Test
  public void happyPath() throws Exception {
    String codeVerifierValue = "::code::";
    byte[] hashedCodeChallenge = MessageDigest.getInstance("SHA-256").digest(codeVerifierValue.getBytes(StandardCharsets.UTF_8));
    String transformedCodeChallenge = BaseEncoding.base64().encode(hashedCodeChallenge);

    CodeChallenge codeChallenge = new CodeChallenge(transformedCodeChallenge, "S256");

    assertTrue(codeVerifier.verify(codeChallenge, codeVerifierValue));
  }

  @Test
  public void wrongCodeVerifierValue() throws Exception {
    String codeVerifierValue = "::code::";
    byte[] hashedCodeChallenge = MessageDigest.getInstance("SHA-256").digest(codeVerifierValue.getBytes(StandardCharsets.UTF_8));
    String transformedCodeChallenge = BaseEncoding.base64Url().encode(hashedCodeChallenge);

    CodeChallenge codeChallenge = new CodeChallenge(transformedCodeChallenge, "S256");

    assertFalse(codeVerifier.verify(codeChallenge, "::codee::"));
  }

  @Test
  public void usingPlainMethod() throws Exception {
    CodeChallenge codeChallenge = new CodeChallenge("::code::", "plain");
    assertTrue(codeVerifier.verify(codeChallenge, "::code::"));
  }

  @Test
  public void codeVerifierProvidedButNoCodeChallengeFound() throws Exception {
    assertFalse(codeVerifier.verify(new CodeChallenge("", ""), "::code::"));
  }

  @Test
  public void codeChallengeProvidedButNoCodeVerifier() throws Exception {
    CodeChallenge codeChallenge = new CodeChallenge("::code::", "plain");
    assertFalse(codeVerifier.verify(codeChallenge, ""));
  }
}