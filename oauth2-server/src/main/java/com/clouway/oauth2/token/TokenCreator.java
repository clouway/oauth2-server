package com.clouway.oauth2.token;

import com.clouway.oauth2.Duration;
import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(BearerTokenCreator.class)
public interface TokenCreator {

  Token create(ProvidedAuthorizationCode providedAuthorizationCode, Duration expirationDuration);

  Token create(ProvidedRefreshToken providedRefreshToken, Duration expirationDuration);
}
