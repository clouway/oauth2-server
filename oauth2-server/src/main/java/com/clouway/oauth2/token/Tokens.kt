package com.clouway.oauth2.token

import com.clouway.oauth2.common.DateTime
import com.google.common.base.Optional

/**
 * Tokens is responsible for issuing and retriving of issued tokens.
 *
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
interface Tokens {
    /**
     * Find token which is not expired till the provided time.
     *
     * @param token then token for which is looked
     * @return an optional token value or absent value if not present
     */
    fun findTokenAvailableAt(
        tokenValue: String,
        instant: DateTime,
    ): Optional<BearerToken>

    /**
     * Refreshes token using the access token.
     *
     * @param token the access token
     * @param when  token is going to be refreshed
     * @return the refreshed token
     */
    fun refreshToken(
        refreshToken: String,
        instant: DateTime,
    ): TokenResponse

    /**
     * Issues a new token for the provided identity.
     *
     * @param tokenRequest:
     * grantType  type of the taken to be issued - JWT or Bearer
     * client     the client to which token will be issued
     * identity   the identity for which token was issued
     * scopes     requested scopes
     * when       the requested time on which it should be issued
     * params
     * @return the newly issued token
     */
    fun issueToken(tokenRequest: TokenRequest): TokenResponse

    /**
     * Revokes token from repository.
     *
     * @param token the token which to be revoked
     */
    fun revokeToken(token: String)
}
