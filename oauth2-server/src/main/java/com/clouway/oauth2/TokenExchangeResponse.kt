package com.clouway.oauth2

import com.clouway.friendlyserve.RsJson
import com.clouway.friendlyserve.RsWrap
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.BearerToken
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class TokenExchangeResponse(
    token: BearerToken,
    idToken: String?,
    issuedTokenType: String?,
    refreshToken: String? = null,
) : RsWrap(RsJson(createToken(token, idToken, issuedTokenType, refreshToken)))

fun createToken(
    token: BearerToken,
    idToken: String?,
    issuedTokenType: String?,
    refreshToken: String? = null,
): JsonElement {
    val json = JsonObject()
    json.addProperty("access_token", token.value)
    // RFC 8693: issued_token_type is RECOMMENDED when different from requested. We include if provided.
    if (issuedTokenType != null) {
        json.addProperty("issued_token_type", issuedTokenType)
    } else {
        // default to access_token when not specified
        json.addProperty("issued_token_type", "urn:ietf:params:oauth:token-type:access_token")
    }
    json.addProperty("token_type", "Bearer")
    json.addProperty("expires_in", token.ttlSeconds(DateTime()))
    // subject_kind is already included in id_token by IdTokenFactory; no need to duplicate here
    if (token.scopes != null) {
        json.addProperty("scope", token.scopes.joinToString(" "))
    }

    // Include id_token when available (OpenID Connect usage)
    if (!idToken.isNullOrEmpty()) {
        json.addProperty("id_token", idToken)
    }

    if (!refreshToken.isNullOrEmpty()) {
        json.addProperty("refresh_token", refreshToken)
    }

    return json
}
