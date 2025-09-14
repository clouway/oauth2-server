package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.Identity
import com.clouway.oauth2.token.SubjectKind
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
                    identity = identity,
                    subjectKind = SubjectKind.USER,
                    scopes = scopes,
                    `when` = instant,
                    params = params,
                ),
            )
		
        if (!response.isSuccessful) {
            return OAuthError.invalidRequest("Token cannot be issued.")
        }
		
        val accessToken = response.accessToken
        val idToken = idTokenFactory
            .newBuilder()
            .issuer(request.header("Host"))
            .audience(client.id)
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
