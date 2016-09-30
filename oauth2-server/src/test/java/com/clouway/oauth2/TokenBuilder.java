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

  private String clientId = "";
  private DateTime createdOn = new DateTime();
  private String value;
  private long timeToLiveInSeconds;
  private TokenType type = TokenType.BEARER;

  public TokenBuilder withValue(String value) {
    this.value = value;
    return this;
  }

  public TokenBuilder timeToLiveInSeconds(long timeToLiveInSeconds) {
    this.timeToLiveInSeconds = timeToLiveInSeconds;
    return this;
  }

  public TokenBuilder createdOn(DateTime createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  public Token build() {
    return new Token(value, type, "", "", clientId, timeToLiveInSeconds, createdOn);
  }

  public TokenBuilder forClient(String clientId) {
    this.clientId = clientId;
    return this;
  }
}
