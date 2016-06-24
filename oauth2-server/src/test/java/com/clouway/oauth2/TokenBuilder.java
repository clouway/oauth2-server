package com.clouway.oauth2;

import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenType;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TokenBuilder {

  public static TokenBuilder aNewToken() {
    return new TokenBuilder();
  }

  private String value;
  private TokenType type = TokenType.BEARER;

  public TokenBuilder withValue(String value) {
    this.value = value;
    return this;
  }

  public Token build() {
    return new Token(value, type, "", "", 10L,new DateTime());
  }

}
