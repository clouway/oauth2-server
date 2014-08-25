package com.example.auth.core.token;

import com.example.auth.app.TokenTimeToLive;
import com.example.auth.core.Clock;
import com.example.auth.core.Duration;
import com.example.auth.core.authorization.ClientAuthorizationRepository;
import com.example.auth.core.authorization.Authorization;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

public class BearerTokenCreator implements TokenCreator {
  private TokenRepository tokenRepository;
  private final TokenGenerator tokenGenerator;
  private ClientAuthorizationRepository repository;
  private final Clock clock;
  private final Duration expirationDuration;

  @Inject
  public BearerTokenCreator(TokenRepository tokenRepository,
                            TokenGenerator tokenGenerator,
                            ClientAuthorizationRepository repository,
                            Clock clock,
                            @TokenTimeToLive Duration expirationDuration) {
    this.tokenRepository = tokenRepository;
    this.tokenGenerator = tokenGenerator;
    this.repository = repository;
    this.clock = clock;
    this.expirationDuration = expirationDuration;
  }

  @Override
  public Token create(String authorizationCode) {

    String userId = fetchUserId(authorizationCode);

    String value = tokenGenerator.generate();

    String type = "bearer";

    Token token = new Token(value, type,userId,  expirationDuration.seconds, clock.now());

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
