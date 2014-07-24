package com.example.auth.core.token;

import com.google.common.base.Optional;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TokenVerifierImpl implements TokenVerifier {

  private TokenRepository repository;

  @Inject
  public TokenVerifierImpl(TokenRepository repository) {

    this.repository = repository;
  }

  @Override
  public Boolean verify(String tokenValue) {
    Optional<Token> tokenOptional = repository.getNotExpiredToken(tokenValue);

    if(tokenOptional.isPresent()){
      return true;
    }
    return false;
  }

//  @Override
//   public Boolean verify(String token) {
//     if (tokens.containsKey(token)) {
//       TokenEntity tokenEntity = tokens.get(token);
//
//       if (clock.now().before(tokenEntity.expiration)) {
//         tokens.put(token, new TokenEntity(tokenEntity.value, tokenEntity.type, clock.nowPlus(expirationDuration)));
//
//         return true;
//       }
//     }
//
//     return false;
//   }
}
