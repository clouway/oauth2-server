package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.friendlyserve.RsJson
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.Tokens
import com.google.gson.JsonObject

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
internal class UserInfoController(
    private val identityFinder: IdentityFinder,
    private val tokens: Tokens,
) : InstantaneousRequest {
    override fun handleAsOf(
        request: Request,
        instantTime: DateTime,
    ): Response {
        val accessToken = request.param("access_token")
		
        val possibleTokenResponse = tokens.findTokenAvailableAt(accessToken, instantTime)
		
        if (!possibleTokenResponse.isPresent) {
            return OAuthError.invalidToken("Access token was not found.")
        }
		
        val token = possibleTokenResponse.get()
		
        val findReq = FindIdentityRequest(
            subject = token.subject,
            instantTime = instantTime,
            clientId = token.clientId,
        )
		
        val o = JsonObject()
        var claims: Map<String, Any> = mutableMapOf()
        when (val res = identityFinder.findIdentity(findReq)) {
            is FindIdentityResult.User -> {
                val identity = res.identity
                o.addProperty("id", identity.id)
                o.addProperty("name", identity.name)
                o.addProperty("email", identity.email)
                o.addProperty("given_name", identity.givenName)
                o.addProperty("family_name", identity.familyName)

                claims = identity.claims
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val serviceAccount = res.serviceAccount
                o.addProperty("id", serviceAccount.clientId)
                o.addProperty("name", serviceAccount.name)
                o.addProperty("email", serviceAccount.clientEmail)
                o.addProperty("given_name", "")
                o.addProperty("family_name", "")

                claims = serviceAccount.claims
            }
            is FindIdentityResult.NotFound -> {
                return OAuthError.invalidGrant("Identity was not found.")
            }
            is FindIdentityResult.Error -> {
                return OAuthError.internalError()
            }
        }

        for (key in claims.keys) {
            val value = claims[key]
			
            if (value is String) {
                o.addProperty(key, value)
            }
            if (value is Number) {
                o.addProperty(key, value)
            }
			
            if (value is Boolean) {
                o.addProperty(key, value)
            }
        }
		
        return RsJson(o)
    }
}
