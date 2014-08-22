package com.example.auth.core.token.refreshtoken;

import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(RefreshTokenProvider.class)
public interface RefreshTokenProvider {

  RefreshToken provide(String existingRefreshToken, String clientId, String clientSecret);

}
