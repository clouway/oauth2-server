package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.client.ClientCredentials
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.Tokens

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class RefreshTokenActivity(
    private val tokens: Tokens,
    private val idTokenFactory: IdTokenFactory,
    private val identityFinder: IdentityFinder,
) : ClientActivity,
    ClientRequest {
    override fun execute(
        client: Client,
        request: Request,
        instant: DateTime,
    ): Response {
        val refreshToken = request.param("refresh_token")
		
        val response = tokens.refreshToken(refreshToken, instant)
        if (!response.isSuccessful) {
            return OAuthError.invalidGrant("Provided refresh_token was not found.")
        }
        val accessToken = response.accessToken
		
        val findReq =
            FindIdentityRequest(
                accessToken.identityId,
                accessToken.grantType,
                instant,
                accessToken.params,
                accessToken.clientId,
            )
		
        when (val res = identityFinder.findIdentity(findReq)) {
            is FindIdentityResult.User -> {
                val idToken = idTokenFactory
                    .newBuilder()
                    .issuer(request.header("Host"))
                    .audience(client.id)
                    .subjectUser(res.identity)
                    .ttl(response.accessToken.ttlSeconds(instant))
                    .issuedAt(instant)
                    .withAccessToken(accessToken.value)
                    .build()
                return BearerTokenResponse(
                    accessToken.value,
                    accessToken.ttlSeconds(instant),
                    response.accessToken.scopes,
                    response.refreshToken,
                    idToken,
                )
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val idToken = idTokenFactory
                    .newBuilder()
                    .issuer(request.header("Host"))
                    .audience(client.id)
                    .subjectServiceAccount(res.serviceAccount)
                    .ttl(response.accessToken.ttlSeconds(instant))
                    .issuedAt(instant)
                    .withAccessToken(accessToken.value)
                    .build()
                return BearerTokenResponse(
                    accessToken.value,
                    accessToken.ttlSeconds(instant),
                    response.accessToken.scopes,
                    response.refreshToken,
                    idToken,
                )
            }
			
            is FindIdentityResult.NotFound -> {
                return OAuthError.invalidGrant("identity was not found")
            }
			
            is FindIdentityResult.Error -> {
                return OAuthError.internalError()
            }
        }
    }

    override fun handleAsOf(
        request: Request,
        credentials: ClientCredentials,
        instant: DateTime,
    ): Response {
        val refreshToken = request.param("refresh_token")
		
        val response = tokens.refreshToken(refreshToken, instant)
        if (!response.isSuccessful) {
            return OAuthError.invalidGrant("Provided refresh_token was not found.")
        }
        val accessToken = response.accessToken

        val findReq =
            FindIdentityRequest(
                accessToken.identityId,
                accessToken.grantType,
                instant,
                accessToken.params,
                accessToken.clientId,
            )
		
        when (val res = identityFinder.findIdentity(findReq)) {
            is FindIdentityResult.User -> {
                val idToken = idTokenFactory
                    .newBuilder()
                    .issuer(request.header("Host"))
                    .audience(credentials.clientId())
                    .subjectUser(res.identity)
                    .ttl(response.accessToken.ttlSeconds(instant))
                    .issuedAt(instant)
                    .withAccessToken(accessToken.value)
                    .build()
                return BearerTokenResponse(
                    accessToken.value,
                    accessToken.ttlSeconds(instant),
                    response.accessToken.scopes,
                    response.refreshToken,
                    idToken,
                )
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val idToken = idTokenFactory
                    .newBuilder()
                    .issuer(request.header("Host"))
                    .audience(credentials.clientId())
                    .subjectServiceAccount(res.serviceAccount)
                    .ttl(response.accessToken.ttlSeconds(instant))
                    .issuedAt(instant)
                    .withAccessToken(accessToken.value)
                    .build()
                return BearerTokenResponse(
                    accessToken.value,
                    accessToken.ttlSeconds(instant),
                    response.accessToken.scopes,
                    response.refreshToken,
                    idToken,
                )
            }

            is FindIdentityResult.Error -> {
                return OAuthError.internalError()
            }
            is FindIdentityResult.NotFound -> {
                return OAuthError.invalidGrant("identity was not found")
            }
        }
    }
}
