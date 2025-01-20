package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.client.JwtKeyStore
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.jws.SignatureFactory
import com.clouway.oauth2.jwt.Jwt
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.Tokens
import com.clouway.oauth2.util.Params
import com.google.gson.Gson
import java.util.Base64

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class JwtController(
    private val signatureFactory: SignatureFactory,
    private val tokens: Tokens,
    private val keyStore: JwtKeyStore,
    private val identityFinder: IdentityFinder,
    private val idTokenFactory: IdTokenFactory,
) : InstantaneousRequest {
    private val gson = Gson()

    override fun handleAsOf(
        request: Request,
        instant: DateTime,
    ): Response {
        val assertion = request.param("assertion")
        val scope = if (request.param("scope") == null) "" else request.param("scope")
		
        val parts: List<String> = assertion.split(".")
		
        // Error should be returned if any of the header parts is missing
        if (parts.size != 3) {
            return OAuthError.invalidRequest("bad request was provided")
        }
		
        val headerContent = parts[0]

        val headerValue = Base64.getUrlDecoder().decode(headerContent).toString(Charsets.UTF_8)
        val content = Base64.getUrlDecoder().decode(parts[1]).toString(Charsets.UTF_8)
        val signatureValue = Base64.getUrlDecoder().decode(parts[2])

        val header =
            gson.fromJson(
                headerValue,
                Jwt.Header::class.java,
            )
        val claimSet =
            gson.fromJson(
                content,
                Jwt.ClaimSet::class.java,
            )
		
        val possibleResponse = keyStore.findKey(header, claimSet)
		
        if (!possibleResponse.isPresent) {
            return OAuthError.invalidGrant("unknown claims")
        }
		
        val serviceAccountKey = possibleResponse.get()
		
        val optSignature = signatureFactory.createSignature(signatureValue, header)
		
        // Unknown signture was provided, so we are returning request as invalid.
        if (!optSignature.isPresent) {
            return OAuthError.invalidRequest("Unknown signature was provided.")
        }
		
        val headerAndContentAsBytes = String.format("%s.%s", parts[0], parts[1]).toByteArray()
		
        if (!optSignature.get().verifyWithPrivateKey(headerAndContentAsBytes, serviceAccountKey)) {
            return OAuthError.invalidGrant("Invalid signature was provided.")
        }
		
        val params = Params().parse(request, "assertion", "scope")

        val scopeValue = if (!claimSet.scope.isNullOrEmpty()) claimSet.scope + " " + scope else scope

        val scopes: Set<String> =
            scopeValue
                .split(
                    " ",
                ).filter { it.isNotEmpty() }
                .toSortedSet()

        when (val res = identityFinder.findIdentity(FindIdentityRequest(claimSet.iss, GrantType.JWT, instant, params, ""))) {
            is FindIdentityResult.User -> {
                return OAuthError.invalidClient("authorization_code client cannot be used for JWT grant")
            }
            is FindIdentityResult.ServiceAccountClient -> {
                val serviceAccount = res.serviceAccount

                if (!serviceAccount.permissions.allowAll) {
                    val allowedScopes =
                        serviceAccount.permissions.scopes
                            .map { it.name }
                            .toSet()

                    // Ensure all requested scopes are within the allowed scopes
                    if (!scopes.all { it in allowedScopes }) {
                        return OAuthError.invalidRequest(
                            "all requested scopes ${scopes.joinToString(
                                " ",
                            )} should be subset of allowed scopes ${allowedScopes.joinToString(" ")}",
                        )
                    }
                }

                val client = Client(claimSet.iss, "", "", emptySet(), false)

                val response =
                    tokens.issueToken(
                        TokenRequest(
                            grantType = GrantType.JWT,
                            client = client,
                            serviceAccount = serviceAccount,
                            scopes = scopes,
                            `when` = instant,
                            params = params,
                        ),
                    )
	            				
                if (!response.isSuccessful) {
                    return OAuthError.invalidRequest("tokens issuing is temporary unavailable")
                }
	            				
                val accessToken = response.accessToken
                val possibleIdToken =
                    idTokenFactory.create(
                        request.header("Host"),
                        client.id,
                        serviceAccount,
                        accessToken.ttlSeconds(instant),
                        instant,
                    )
                if (possibleIdToken.isPresent) {
                    return BearerTokenResponse(
                        accessToken.value,
                        accessToken.ttlSeconds(instant),
                        accessToken.scopes,
                        response.refreshToken,
                        possibleIdToken.get(),
                    )
                }
                return BearerTokenResponse(
                    accessToken.value,
                    accessToken.ttlSeconds(instant),
                    accessToken.scopes,
                    response.refreshToken,
                )
            }

            is FindIdentityResult.NotFound -> {
                return OAuthError.invalidGrant("unknown identity")
            }

            is FindIdentityResult.Error -> {
                return OAuthError.internalError()
            }
        }
    }
}
