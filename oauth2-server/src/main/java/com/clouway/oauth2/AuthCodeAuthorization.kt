package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.authorization.ClientAuthorizer
import com.clouway.oauth2.authorization.FindAuthorizationResult
import com.clouway.oauth2.client.ClientCredentials
import com.clouway.oauth2.common.DateTime

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
internal class AuthCodeAuthorization(
	private val clientAuthorizer: ClientAuthorizer,
	private val clientActivity: AuthorizedClientActivity
) : ClientRequest {
	
	override fun handleAsOf(request: Request, credentials: ClientCredentials, instant: DateTime): Response {
		val authCode = request.param("code")
		when (val result = clientAuthorizer.findAuthorization(credentials.clientId(), authCode, instant)) {
			is FindAuthorizationResult.Success -> {
				
				// Make sure that client credentials are matching
				// to ensure that the client is same as the one
				// that was issued the authorization.
				if (result.authorization.clientId != result.client.id) {
					return OAuthError.unauthorizedClient("Client credentials not match")
				}
				
				if (!result.client.credentialsMatch(credentials)) {
					return OAuthError.unauthorizedClient("Unknown client '${credentials.clientId()}'")
				}
				return clientActivity.execute(result.authorization, result.client, request, instant)
			}
			is FindAuthorizationResult.NotFound -> {
				return OAuthError.invalidGrant("Authorization was not found.")
			}
			is FindAuthorizationResult.ClientNotFound -> {
				return OAuthError.unauthorizedClient("Unknown client '${credentials.clientId()}'")
			}
		}
	}
}