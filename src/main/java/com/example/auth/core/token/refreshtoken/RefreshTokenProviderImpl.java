package com.example.auth.core.token.refreshtoken;

import com.example.auth.app.GenerateNewRefreshToken;
import com.example.auth.core.Clock;
import com.example.auth.core.token.TokenGenerator;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Date;

import static com.example.auth.core.token.refreshtoken.RefreshToken.aNewRefreshToken;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RefreshTokenProviderImpl implements RefreshTokenProvider {

  private final RefreshTokenRepository refreshTokenRepository;
  private final Clock clock;
  private final Provider<Boolean> generateNewRefreshToken;
  private final TokenGenerator tokenGenerator;

  @Inject
  public RefreshTokenProviderImpl(RefreshTokenRepository refreshTokenRepository,
                                  Clock clock,
                                  @GenerateNewRefreshToken Provider<Boolean> generateNewRefreshToken,
                                  TokenGenerator tokenGenerator) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.clock = clock;
    this.generateNewRefreshToken = generateNewRefreshToken;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public RefreshToken provide(String existingRefreshToken, String clientId, String clientSecret) {
    if (generateNewRefreshToken.get()) {

      clearOldRefreshToken(existingRefreshToken);

      String token = tokenGenerator.generate();
      Date creationDate = clock.now();

      RefreshToken refreshToken = aNewRefreshToken(token, clientId, clientSecret).creationDate(creationDate).build();

      refreshTokenRepository.save(refreshToken);

      return refreshToken;
    }

    String refreshTokenValue = existingRefreshToken;
    if (Strings.isNullOrEmpty(refreshTokenValue)) {
      refreshTokenValue = tokenGenerator.generate();
    }

    return aNewRefreshToken(refreshTokenValue, clientId, clientSecret).build();
  }

  private void clearOldRefreshToken(String refreshToken) {
    if (!Strings.isNullOrEmpty(refreshToken)) {
      refreshTokenRepository.delete(refreshToken);
    }
  }
}
