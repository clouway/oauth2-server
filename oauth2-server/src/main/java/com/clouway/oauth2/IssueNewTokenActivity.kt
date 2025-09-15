package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.Identity
import com.clouway.oauth2.token.Subject
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.Tokens

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity(
    private val tokens: Tokens,
    private val idTokenFactory: IdTokenFactory,
) : AuthorizedIdentityActivity {
    override fun execute(
        client: Client,
        identity: Identity,
        scopes: Set<String>,
        request: Request,
        instant: DateTime,
        params: Map<String, String>,
    ): Response {
        val response =
            tokens.issueToken(
                TokenRequest(
                    grantType = GrantType.AUTHORIZATION_CODE,
                    client = client,
                    subject = Subject.User(identity.id),
                    scopes = scopes,
                    `when` = instant,
                    claims = identity.claims,
                    params = params,
                ),
            )
		
        if (!response.isSuccessful) {
            return OAuthError.invalidRequest("Token cannot be issued.")
        }

        // The issuer can be configured through environment variable IDP_CONFIG_JWT_ISSUER.
        // If not set, the issuer is constructed from the request (scheme and host).
        val issuer = LocalConfig.jwtIssuerName() ?: request.buildIssuer()

        // Prefer the standard resource parameter to indicate which API was requested by the client. If not
        // present, fall back to clientId for backward compatibility.
        // See https://tools.ietf.org/html/rfc8707
        var audience = if (params.contains("resource")) params.getValue("resource") else request.param("resource")
        if (audience.isNullOrEmpty()) {
            audience = client.id
        }

        val accessToken = response.accessToken
        val idToken =
            idTokenFactory
                .newBuilder()
                .issuer(issuer)
                .audience(audience)
                .subjectUser(identity)
                .ttl(accessToken.ttlSeconds(instant))
                .issuedAt(instant)
                .withAccessToken(accessToken.value)
                .build()
		
        // Compute at_hash when id_token is present and access_token is issued with it
        return BearerTokenResponse(
            accessToken.value,
            accessToken.ttlSeconds(instant),
            accessToken.scopes,
            response.refreshToken,
            idToken,
        )
    }
}
