package com.clouway.oauth2.exampleapp.storage

import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.common.Duration
import com.clouway.oauth2.token.BearerToken
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.TokenGenerator
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.TokenResponse
import com.clouway.oauth2.token.Tokens
import com.google.common.base.Optional
import com.google.common.collect.Maps
import com.google.inject.Inject

/**
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
class InMemoryTokens
    @Inject
    constructor(
        private val tokenGenerator: TokenGenerator,
        private val timeToLive: Duration?,
    ) : Tokens {
        private val tokens: MutableMap<String?, BearerToken> = Maps.newHashMap()
        private val refreshTokenToAccessToken: MutableMap<String, String> = Maps.newHashMap()

        override fun findTokenAvailableAt(
            tokenValue: String,
            instant: DateTime,
        ): Optional<BearerToken> {
            if (tokens.containsKey(tokenValue)) {
                val token = tokens[tokenValue]
			
                if (!token!!.expiresAt(instant)) {
                    // update token expirationDate time
                    // remove the current token
                    tokens.remove(tokenValue)
                    // new instance
                    val updatedToken =
                        BearerToken(
                            token.value,
                            token.grantType,
                            token.subject,
                            token.clientId,
                            token.email,
                            emptySet(),
                            instant,
                            Maps.newHashMap(),
                        )
                    // add the new token
                    tokens[tokenValue] = updatedToken
				
                    return Optional.of(token)
                }
            }
		
            return Optional.absent()
        }

        override fun refreshToken(
            refreshToken: String,
            instant: DateTime,
        ): TokenResponse {
            if (refreshTokenToAccessToken.containsKey(refreshToken)) {
                val accessToken = refreshTokenToAccessToken[refreshToken]
                val oldToken = tokens[accessToken]
                tokens.remove(accessToken)
			
                val newTokenValue = tokenGenerator.generate()
                val updatedToken =
                    BearerToken(
                        newTokenValue,
                        oldToken!!.grantType,
                        oldToken.subject,
                        oldToken.clientId,
                        oldToken.email,
                        emptySet(),
                        instant,
                        oldToken.params,
                    )
			
                tokens[newTokenValue] = updatedToken
                refreshTokenToAccessToken[refreshToken] = newTokenValue
			
                return TokenResponse(true, updatedToken, refreshToken)
            }
		
            return TokenResponse(false, null, "")
        }

        override fun issueToken(tokenRequest: TokenRequest): TokenResponse {
            val token = tokenGenerator.generate()
            val refreshTokenValue = tokenGenerator.generate()
		
            val bearerToken = when (tokenRequest.grantType) {
                GrantType.JWT -> {
                    val subj = tokenRequest.subject as com.clouway.oauth2.token.Subject.ServiceAccount
                    BearerToken(
                        token,
                        GrantType.JWT,
                        subj,
                        tokenRequest.client.id,
                        "", // email may be looked up elsewhere in real impl
                        tokenRequest.scopes,
                        tokenRequest.`when`,
                        tokenRequest.params,
                    )
                }
                else -> {
                    val subj = tokenRequest.subject as com.clouway.oauth2.token.Subject.User
                    BearerToken(
                        token,
                        GrantType.AUTHORIZATION_CODE,
                        subj,
                        tokenRequest.client.id,
                        "",
                        tokenRequest.scopes,
                        tokenRequest.`when`,
                        tokenRequest.params,
                    )
                }
            }

            tokens[token] = bearerToken
		
            return TokenResponse(true, bearerToken, refreshTokenValue)
        }

        override fun revokeToken(token: String) {
            tokens.remove(token)
        }
    }
