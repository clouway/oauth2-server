package com.clouway.oauth2.token;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenRequestEqualityTest {
  @Test
  public void areEqual() {
    EqualsVerifier.forClass(TokenRequest.class).verify();
  }
}