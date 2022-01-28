package com.clouway.oauth2.authorization

import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime

/**
 * ClientAuthorizationRepository is a repository which is keeping records for the authroziations
 * that are performed for the client applications.
 *
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
interface ClientAuthorizer {
	
	/**
	 * Authorizes client by issuing a new authorization code to be used
	 * latter in the exchange for a token. 
	 */
	fun authorizeClient(req: AuthorizationRequest): ClientAuthorizationResult
	
	/**
	 * Finds authorization that is associated with the provided authCode.
	 *
	 * @param client   the client for which authorization was issued
	 * @param authCode the code of the authorization
	 * @param instant  the time on which check is performed
	 * @return the authorization or absent value
	 */
	fun findAuthorization(clientId: String, authCode: String, instant: DateTime): FindAuthorizationResult
	
	
}

sealed class ClientAuthorizationResult {
	data class Success(val client: Client, val authCode: String) : ClientAuthorizationResult()
	
	data class AccessDenied(val reason: String) : ClientAuthorizationResult()
	
	class ClientNotFound() : ClientAuthorizationResult()
	
	data class Error(val message: String?) : ClientAuthorizationResult()
}

sealed class FindAuthorizationResult {
	
	data class Success(val authorization: Authorization, val client: Client) : FindAuthorizationResult()
	
	class NotFound() : FindAuthorizationResult()
	
	class ClientNotFound() : FindAuthorizationResult()
	
}