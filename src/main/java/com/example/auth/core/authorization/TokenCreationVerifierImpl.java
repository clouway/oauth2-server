package com.example.auth.core.authorization;

import com.example.auth.core.ClientAuthorizationRequest;
import com.example.auth.core.Clock;
import com.example.auth.core.token.refreshtoken.RefreshToken;
import com.example.auth.core.token.refreshtoken.RefreshTokenRepository;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TokenCreationVerifierImpl implements TokenCreationVerifier {

  private ClientAuthorizationRepository repository;
  private Clock clock;
  private RefreshTokenRepository refreshTokenRepository;

  @Inject
  public TokenCreationVerifierImpl(ClientAuthorizationRepository repository, Clock clock, RefreshTokenRepository refreshTokenRepository) {

    this.repository = repository;
    this.clock = clock;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public Boolean verify(String authCode, String clintId) {
    Optional<ClientAuthorizationRequest> authorization = repository.findByCode(authCode);

    if (authorization.isPresent()) {

      ClientAuthorizationRequest authorizationRequest = authorization.get();

      if (authorizationRequest.clientId.equals(clintId) && authorizationRequest.isNotUsed()) {

        authorizationRequest.usedOn(clock.now());
        repository.update(authorizationRequest);

        return true;
      }
    }

    return false;
  }

  @Override
  public Boolean verifyRefreshToken(String clientId, String clientSecret, String refreshToken) {

    Optional<RefreshToken> token = refreshTokenRepository.load(refreshToken);


    if (token.isPresent()) {

      RefreshToken requiredToken = new RefreshToken(refreshToken, clientId, clientSecret);

      return requiredToken.equals(token.get());
    }


    return false;
  }
}
