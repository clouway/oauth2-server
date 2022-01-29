package com.clouway.oauth2.exampleapp.storage

import com.clouway.oauth2.authorization.Authorization
import com.clouway.oauth2.authorization.AuthorizationRequest
import com.clouway.oauth2.authorization.ClientAuthorizationResult
import com.clouway.oauth2.authorization.ClientAuthorizer
import com.clouway.oauth2.authorization.FindAuthorizationResult
import com.clouway.oauth2.client.ClientFinder
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.UrlSafeTokenGenerator
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
internal class InMemoryClientAuthorizer(private val clientFinder: ClientFinder) : ClientAuthorizer {
	private val authorizations: MutableMap<String, Authorization> = ConcurrentHashMap()
	override fun findAuthorization(clientId: String, authCode: String, instant: DateTime): FindAuthorizationResult {
		val authorization = authorizations[authCode]
			?: return FindAuthorizationResult.NotFound()
		// No authorization was found for that code ?
		val possibleClient = clientFinder.findClient(clientId)
		if (!possibleClient.isPresent) {
			return FindAuthorizationResult.ClientNotFound()
		}
		authorizations.remove(authCode)
		return FindAuthorizationResult.Success(authorization, possibleClient.get())
	}
	
	private fun register(authorization: Authorization) {
		authorizations[authorization.code] = authorization
	}
	
	
	override fun authorizeClient(req: AuthorizationRequest): ClientAuthorizationResult {
		val possibleClient = clientFinder.findClient(req.clientId)
		if (!possibleClient.isPresent) {
			return ClientAuthorizationResult.ClientNotFound()
		}
		
		val code = UrlSafeTokenGenerator().generate()
		val client = possibleClient.get()
		
		val auth = Authorization(
			responseType = req.responseType,
			clientId = req.clientId,
			identityId = req.identityId,
			code = code,
			scopes = req.scopes,
			redirectUrls = client.redirectURLs,
			codeChallenge = req.codeChallenge,
			params = req.params
		)
		
		authorizations[code] = auth
		
		return ClientAuthorizationResult.Success(client, code)
	}
}