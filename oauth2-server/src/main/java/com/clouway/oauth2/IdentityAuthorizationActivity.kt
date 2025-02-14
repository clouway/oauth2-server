package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.authorization.Authorization
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdentityFinder

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
class IdentityAuthorizationActivity(
    private val identityFinder: IdentityFinder,
    private val authorizedIdentityActivity: AuthorizedIdentityActivity,
) : AuthorizedClientActivity {
    override fun execute(
        authorization: Authorization,
        client: Client,
        request: Request,
        instant: DateTime,
    ): Response {
        val findIdentityRequest =
            FindIdentityRequest(
                authorization.identityId,
                GrantType.AUTHORIZATION_CODE,
                instant,
                authorization.params,
                authorization.clientId,
            )

        when (val res = identityFinder.findIdentity(findIdentityRequest)) {
            is FindIdentityResult.User -> {
                val identity = res.identity
                return authorizedIdentityActivity.execute(
                    client,
                    identity,
                    authorization.scopes,
                    request,
                    instant,
                    authorization.params,
                )
            }
            is FindIdentityResult.ServiceAccountClient -> {
                return OAuthError.invalidClient("service accounts cannot be used for authorization_code grant")
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
