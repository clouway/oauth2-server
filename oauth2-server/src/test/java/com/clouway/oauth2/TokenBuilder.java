package com.clouway.oauth2;

import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenType;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TokenBuilder {


  public static TokenBuilder aNewToken() {
    return new TokenBuilder();
  }

  private String clientId = "";
  private DateTime expiresAt = new DateTime();
  private String value;
  private TokenType type = TokenType.BEARER;

  public TokenBuilder withValue(String value) {
    this.value = value;
    return this;
  }

  public TokenBuilder expiresAt(DateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  public Token build() {
    return new Token(value, type, GrantType.AUTHORIZATION_CODE, "", clientId, expiresAt);
  }

  public TokenBuilder forClient(String clientId) {
    this.clientId = clientId;
    return this;
  }
}
