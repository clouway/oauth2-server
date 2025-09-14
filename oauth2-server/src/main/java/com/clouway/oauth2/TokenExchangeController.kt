package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.friendlyserve.RsText
import com.clouway.oauth2.authorization.ClientAuthorizer
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.client.ClientCredentials
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.BearerToken
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.Tokens
import com.google.common.base.Optional

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class TokenExchangeController(
    private val tokens: Tokens,
    private val identityFinder: IdentityFinder,
    private val idTokenFactory: IdTokenFactory,
) : ClientRequest {
    override fun handleAsOf(
        request: Request,
        credentials: ClientCredentials,
        instant: DateTime,
    ): Response {
        val subjectToken: String? = request.param("subject_token")
        val subjectTokenType: String? = request.param("subject_token_type")
        val requestedTokenType: String? = request.param("requested_token_type")
        val scope: String? = request.param("scope")
        val audience: String? = request.param("audience")

        if (subjectToken.isNullOrEmpty()) {
            return OAuthError.invalidRequest("subject_token is required")
        }

        // Validate subject token
        val token: Optional<BearerToken> = tokens.findTokenAvailableAt(subjectToken, instant)
        if (!token.isPresent) {
            return OAuthError.invalidToken("Subject token is invalid or expired")
        }

        // Validate subject token type (support access_token and id_token as subject)
        if (subjectTokenType != null && subjectTokenType != "urn:ietf:params:oauth:token-type:access_token" &&
            subjectTokenType != "urn:ietf:params:oauth:token-type:id_token"
        ) {
            return OAuthError.unsupportedTokenType("Subject token type not supported")
        }

        // Determine requested token types; support access_token, id_token, refresh_token (space-separated per RFC 8693)
        val requestedTypes: Set<String> =
            if (!requestedTokenType.isNullOrBlank()) {
                requestedTokenType.split(" ").filter { it.isNotBlank() }.toSet()
            } else {
                setOf("urn:ietf:params:oauth:token-type:access_token")
            }

        val supportedTypes =
            setOf(
                "urn:ietf:params:oauth:token-type:access_token",
                "urn:ietf:params:oauth:token-type:id_token",
                "urn:ietf:params:oauth:token-type:refresh_token",
            )
        if (!requestedTypes.all { supportedTypes.contains(it) }) {
            return OAuthError.invalidRequest("requested_token_type not supported")
        }

        // Determine requested scopes; default to subject token scopes when omitted
        val requestedScopes: Set<String> =
            if (!scope.isNullOrBlank()) scope.split(" ").filter { it.isNotBlank() }.toSet() else token.get().scopes ?: emptySet()

        // Enforce subset: all requested scopes must be within subject token scopes
        val subjectScopes = token.get().scopes ?: emptySet()
        if (!requestedScopes.all { subjectScopes.contains(it) }) {
            return OAuthError.invalidScope("requested scopes must be subset of subject token scopes")
        }

        // Find identity behind the subject token before issuing
        val subject = token.get()
        val identityRes =
            identityFinder.findIdentity(
                FindIdentityRequest(
                    subject.subjectKind,
                    subject.identityId,
                    subject.grantType,
                    instant,
                    subject.params,
                    subject.clientId,
                ),
            )

        // Build params per RFC 8693 to reflect exchange context
        val params = mutableMapOf<String, String>()
        params["subject_token"] = subjectToken
        subjectTokenType?.let { params["subject_token_type"] = it }
        if (requestedTypes.isNotEmpty()) {
            params["requested_token_type"] = requestedTypes.joinToString(" ")
        }
        audience?.let { params["audience"] = it }

        when (identityRes) {
            is FindIdentityResult.User -> {
                val tokenResponse =
                    tokens.issueToken(
                        TokenRequest(
                            grantType = GrantType.TOKEN_EXCHANGE,
                            client =
                                Client(credentials.clientId(), "", "", emptySet(), false),
                            identity = identityRes.identity,
                            serviceAccount = null,
                            subjectKind = com.clouway.oauth2.token.SubjectKind.USER,
                            scopes = requestedScopes,
                            `when` = instant,
                            params = params,
                        ),
                    )

                if (!tokenResponse.isSuccessful) {
                    return OAuthError.invalidRequest("Token cannot be issued.")
                }

                val accessToken = tokenResponse.accessToken
                val idToken =
                    idTokenFactory
                        .newBuilder()
                        .issuer(request.header("Host"))
                        .audience(credentials.clientId())
                        .subjectUser(identityRes.identity)
                        .ttl(accessToken.ttlSeconds(instant))
                        .issuedAt(instant)
                        .withAccessToken(accessToken.value)
                        .build()
                val idtRequested = requestedTypes.contains("urn:ietf:params:oauth:token-type:id_token")
                val idt = if (idtRequested) idToken else null
                val refreshRequested = requestedTypes.contains("urn:ietf:params:oauth:token-type:refresh_token")
                val refresh = if (refreshRequested) tokenResponse.refreshToken else null
                val issuedType = requestedTypes.firstOrNull() ?: "urn:ietf:params:oauth:token-type:access_token"
                return TokenExchangeResponse(accessToken, idt, issuedType, refresh)
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val tokenResponse =
                    tokens.issueToken(
                        TokenRequest(
                            grantType = GrantType.TOKEN_EXCHANGE,
                            client =
                                Client(credentials.clientId(), credentials.clientSecret(), "", emptySet(), false),
                            identity = null,
                            serviceAccount = identityRes.serviceAccount,
                            subjectKind = com.clouway.oauth2.token.SubjectKind.SERVICE_ACCOUNT,
                            scopes = requestedScopes,
                            `when` = instant,
                            params = params,
                        ),
                    )

                if (!tokenResponse.isSuccessful) {
                    return OAuthError.invalidRequest("Token cannot be issued.")
                }

                val accessToken = tokenResponse.accessToken
                val idToken =
                    idTokenFactory
                        .newBuilder()
                        .issuer(request.header("Host"))
                        .audience(credentials.clientId())
                        .subjectServiceAccount(identityRes.serviceAccount)
                        .ttl(accessToken.ttlSeconds(instant))
                        .issuedAt(instant)
                        .withAccessToken(accessToken.value)
                        .build()
                val idtRequested = requestedTypes.contains("urn:ietf:params:oauth:token-type:id_token")
                val idt = if (idtRequested) idToken else null
                val refreshRequested = requestedTypes.contains("urn:ietf:params:oauth:token-type:refresh_token")
                val refresh = if (refreshRequested) tokenResponse.refreshToken else null
                val issuedType = requestedTypes.firstOrNull() ?: "urn:ietf:params:oauth:token-type:access_token"
                return TokenExchangeResponse(accessToken, idt, issuedType, refresh)
            }
            is FindIdentityResult.NotFound -> {
                return OAuthError.invalidGrant("identity was not found")
            }
            is FindIdentityResult.Error -> {
                return OAuthError.internalError()
            }
        }
    }
}
