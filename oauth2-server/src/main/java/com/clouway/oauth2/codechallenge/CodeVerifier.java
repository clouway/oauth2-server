package com.clouway.oauth2.codechallenge;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public interface CodeVerifier {

  /**
   * Verify the given codeChallenge with the codeVerifier
   *
   * @param codeChallenge the code challenge provided upon authorization
   * @param codeVerifier  the code verifier provided on a request for new token
   * @return true if the two values match false if they don't
   */
  boolean verify(CodeChallenge codeChallenge, String codeVerifier);
}
