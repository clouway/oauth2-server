package com.clouway.oauth2.token;

import com.clouway.oauth2.Clock;
import com.clouway.oauth2.Duration;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

class BearerTokenCreator implements TokenCreator {
  private final TokenRepository tokenRepository;
  private final TokenGenerator tokenGenerator;
  private final TokenSecurity tokenSecurity;
  private final ClientAuthorizationRepository repository;
  private final Clock clock;
  private RefreshTokenGenerator refreshTokenGenerator;

  @Inject
  public BearerTokenCreator(TokenRepository tokenRepository,
                            TokenGenerator tokenGenerator,
                            TokenSecurity tokenSecurity,
                            ClientAuthorizationRepository repository,
                            Clock clock,
                            RefreshTokenGenerator refreshTokenGenerator) {
    this.tokenRepository = tokenRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenSecurity = tokenSecurity;
    this.repository = repository;
    this.clock = clock;
    this.refreshTokenGenerator = refreshTokenGenerator;
  }


  @Override
  public Token create(ProvidedAuthorizationCode providedAuthorizationCode, Duration expirationDuration) {

    tokenSecurity.validate(providedAuthorizationCode);

    String userId = fetchUserId(providedAuthorizationCode.value);

    return createToken(userId, "", expirationDuration);
  }

  @Override
  public Token create(ProvidedRefreshToken providedRefreshToken, Duration expirationDuration) {


    Optional<Token> existingToken = tokenRepository.findByRefreshTokenCode(providedRefreshToken.value);

    if (!existingToken.isPresent()) {
      throw new TokenErrorResponse("invalid_client", "Invalid refresh token request!");
    }

    tokenSecurity.authenticateClient(providedRefreshToken.clientId, providedRefreshToken.clientSecret);

    return createToken(existingToken.get().userId, providedRefreshToken.value, expirationDuration);
  }


  private Token createToken(String userId, String existingRefreshToken, Duration expirationDuration) {

    String value = tokenGenerator.generate();
    String refreshToken = refreshTokenGenerator.generate(existingRefreshToken);

    String type = "bearer";

    Token token = new Token(value, type, refreshToken, userId, expirationDuration.seconds, clock.now());

    tokenRepository.save(token);

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
