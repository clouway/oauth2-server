package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.friendlyserve.RsJson
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.Subject
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
        val req =
            FindIdentityRequest(
                subject = token.subject,
                instantTime = instantTime,
                clientId = token.clientId,
            )
		
        when (val res = identityFinder.findIdentity(req)) {
            is FindIdentityResult.User -> {
                val identity = res.identity
                val host = request.header("Host")
                val idToken =
                    idTokenFactory
                        .newBuilder()
                        .issuer(host)
                        .audience(token.clientId)
                        .subjectUser(identity)
                        .ttl(token.ttlSeconds(instantTime))
                        .issuedAt(instantTime)
                        .withAccessToken(token.value)
                        .build()
					
                val o = JsonObject()
                o.addProperty("azp", token.clientId)
                o.addProperty("aud", token.clientId)
                val sub =
                    when (val s = token.subject) {
                        is Subject.User -> s.id
                        is Subject.ServiceAccount -> s.id
                    }
                o.addProperty("sub", sub)
                o.addProperty("exp", token.expirationTimestamp())
                o.addProperty("expires_in", token.ttlSeconds(instantTime))
                o.addProperty("email", token.email)
					
                if (token.scopes.isNotEmpty()) {
                    o.addProperty("scope", Joiner.on(" ").join(token.scopes))
                }
					
                o.addProperty("id_token", idToken)
					
                return RsJson(o)
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val host = request.header("Host")
                val idToken =
                    idTokenFactory
                        .newBuilder()
                        .issuer(host)
                        .audience(token.clientId)
                        .subjectServiceAccount(res.serviceAccount)
                        .ttl(token.ttlSeconds(instantTime))
                        .issuedAt(instantTime)
                        .withAccessToken(token.value)
                        .build()
	            					
                val o = JsonObject()
                o.addProperty("azp", token.clientId)
                o.addProperty("aud", token.clientId)
                val sub =
                    when (val s = token.subject) {
                        is Subject.User -> s.id
                        is Subject.ServiceAccount -> s.id
                    }
                o.addProperty("sub", sub)
                o.addProperty("exp", token.expirationTimestamp())
                o.addProperty("expires_in", token.ttlSeconds(instantTime))
                o.addProperty("email", token.email)
	            					
                if (token.scopes.isNotEmpty()) {
                    o.addProperty("scope", Joiner.on(" ").join(token.scopes))
                }
	            					
                o.addProperty("id_token", idToken)
	            					
                return RsJson(o)
            }
            else -> {
                return OAuthError.invalidRequest("Identity not found")
            }
        }
    }
}
