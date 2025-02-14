package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.friendlyserve.RsJson
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.Tokens
import com.google.common.base.Joiner
import com.google.common.collect.Maps
import com.google.gson.JsonObject

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
internal class TokenInfoController(
    private val tokens: Tokens,
    private val identityFinder: IdentityFinder,
    private val idTokenFactory: IdTokenFactory,
) : InstantaneousRequest {
    override fun handleAsOf(
        request: Request,
        instantTime: DateTime,
    ): Response {
        val accessToken = request.param("access_token")
		
        val possibleToken = tokens.findTokenAvailableAt(accessToken, instantTime)
        if (!possibleToken.isPresent) {
            return OAuthError.invalidRequest("Access token not found")
        }
		
        val token = possibleToken.get()
        val params = token.params ?: Maps.newHashMap()
		
        val req =
            FindIdentityRequest(
                token.identityId,
                token.grantType,
                instantTime,
                params,
                token.clientId,
            )
		
        when (val res = identityFinder.findIdentity(req)) {
            is FindIdentityResult.User -> {
                val identity = res.identity
                val host = request.header("Host")
                val possibleIdToken =
                    idTokenFactory.create(
                        host,
                        token.clientId,
                        identity,
                        token.ttlSeconds(instantTime),
                        instantTime,
                    )
					
                val o = JsonObject()
                o.addProperty("azp", token.clientId)
                o.addProperty("aud", token.clientId)
                o.addProperty("sub", token.identityId)
                o.addProperty("exp", token.expirationTimestamp())
                o.addProperty("expires_in", token.ttlSeconds(instantTime))
                o.addProperty("email", token.email)
					
                if (token.scopes.isNotEmpty()) {
                    o.addProperty("scope", Joiner.on(" ").join(token.scopes))
                }
					
                if (possibleIdToken.isPresent) {
                    o.addProperty("id_token", possibleIdToken.get())
                }
					
                return RsJson(o)
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val host = request.header("Host")
                val possibleIdToken =
                    idTokenFactory.create(
                        host,
                        token.clientId,
                        res.serviceAccount,
                        token.ttlSeconds(instantTime),
                        instantTime,
                    )
	            					
                val o = JsonObject()
                o.addProperty("azp", token.clientId)
                o.addProperty("aud", token.clientId)
                o.addProperty("sub", token.identityId)
                o.addProperty("exp", token.expirationTimestamp())
                o.addProperty("expires_in", token.ttlSeconds(instantTime))
                o.addProperty("email", token.email)
	            					
                if (token.scopes.isNotEmpty()) {
                    o.addProperty("scope", Joiner.on(" ").join(token.scopes))
                }
	            					
                if (possibleIdToken.isPresent) {
                    o.addProperty("id_token", possibleIdToken.get())
                }
	            					
                return RsJson(o)
            }
            else -> {
                return OAuthError.invalidRequest("Identity not found")
            }
        }
    }
}
