package com.clouway.oauth2.token;

/**
 * TokenResponse is a response which is returned when token is issued or refreshed.
 * <p/>
 * Sometimes token cannot be issue dueerror and this is indicated using the {@link TokenResponse#isSuccessful()} method
 * call.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class TokenResponse {
  private final boolean successful;

  public final String accessToken;
  public final String refreshToken;
  public final Long ttlInSeconds;

  public TokenResponse(boolean successful, String accessToken, String refreshToken, Long ttlInSeconds) {
    this.successful = successful;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.ttlInSeconds = ttlInSeconds;
  }

  public boolean isSuccessful() {
    return successful;
  }
}
