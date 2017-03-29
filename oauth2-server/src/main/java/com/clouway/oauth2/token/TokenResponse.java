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

  public final BearerToken accessToken;
  public final String refreshToken;
  public final String idToken;

  public TokenResponse(boolean successful, BearerToken accessToken, String refreshToken, String idToken) {
    this.successful = successful;
    this.refreshToken = refreshToken;
    this.accessToken = accessToken;
    this.idToken = idToken;
  }

  public boolean isSuccessful() {
    return successful;
  }

}
