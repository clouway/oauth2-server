package com.example.auth.core.token;

import com.example.auth.app.TokenTimeToLive;
import com.example.auth.core.Clock;
import com.example.auth.core.Duration;
import com.example.auth.core.authorization.Authorization;
import com.example.auth.core.authorization.ClientAuthorizationRepository;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

class BearerTokenCreator implements TokenCreator {
  private TokenRepository tokenRepository;
  private final TokenGenerator tokenGenerator;
  private TokenSecurity tokenSecurity;
  private ClientAuthorizationRepository repository;
  private final Clock clock;
  private RefreshTokenGenerator refreshTokenGenerator;
  private Provider<Boolean> generateNewRefreshToken;
  private final Duration expirationDuration;

  @Inject
  public BearerTokenCreator(TokenRepository tokenRepository,
                            TokenGenerator tokenGenerator,
                            TokenSecurity tokenSecurity,
                            ClientAuthorizationRepository repository,
                            Clock clock,
                            RefreshTokenGenerator refreshTokenGenerator,
                            @TokenTimeToLive Duration expirationDuration) {
    this.tokenRepository = tokenRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenSecurity = tokenSecurity;
    this.repository = repository;
    this.clock = clock;
    this.refreshTokenGenerator = refreshTokenGenerator;
    this.expirationDuration = expirationDuration;
  }


  private Token createToken(String userId, String existingRefreshToken) {

    String value = tokenGenerator.generate();
    String refreshToken = refreshTokenGenerator.generate(existingRefreshToken);

    String type = "bearer";

    Token token = new Token(value, type, refreshToken, userId, expirationDuration.seconds, clock.now());

    tokenRepository.save(token);

    return token;
  }

  @Override
  public Token create(ProvidedAuthorizationCode providedAuthorizationCode) {

    tokenSecurity.validate(providedAuthorizationCode);

    String userId = fetchUserId(providedAuthorizationCode.value);

    Token token = createToken(userId, "");

    return token;
  }

  @Override
  public Token create(ProvidedRefreshToken providedRefreshToken) {


    Optional<Token> existingToken = tokenRepository.findByRefreshTokenCode(providedRefreshToken.value);

    if (!existingToken.isPresent()) {
      throw new TokenErrorResponse("invalid_client", "Invalid refresh token request!");
    }

    tokenSecurity.authenticateClient(providedRefreshToken.clientId, providedRefreshToken.clientSecret);

    Token token = createToken(existingToken.get().userId, providedRefreshToken.value);

    return token;
  }


  private String fetchUserId(String authorizationCode) {
    String userId = "";
    Optional<Authorization> authorization = repository.findByCode(authorizationCode);

    if (authorization.isPresent()) {
      userId = authorization.get().userId;
    }

    return userId;
  }
}
