package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.friendlyserve.RsRedirect
import com.clouway.oauth2.authorization.AuthorizationRequest
import com.clouway.oauth2.authorization.ClientAuthorizationResult
import com.clouway.oauth2.authorization.ClientAuthorizer
import com.clouway.oauth2.codechallenge.CodeChallenge
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.util.Params
import com.github.mobiletoly.urlsome.Urlsome
import com.google.common.base.Splitter
import com.google.common.collect.Sets
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
internal class ClientAuthorizationActivity(
    private val clientAuthorizer: ClientAuthorizer,
) : IdentityActivity {
    private val params = Params()

    override fun execute(
        subjectId: String,
        request: Request,
        instantTime: DateTime,
    ): Response {
        val responseType = request.param("response_type")
        val clientId = request.param("client_id")
        val requestedUrl = request.param("redirect_uri")
        val state = request.param("state")
        val scope = if (request.param("scope") == null) "" else request.param("scope")
        val codeChallengeValue = if (request.param("code_challenge") == null) "" else request.param("code_challenge")
        val codeVerifierMethod =
            if (request.param("code_challenge_method") == null) "" else request.param("code_challenge_method")
        val codeChallenge = CodeChallenge(codeChallengeValue, codeVerifierMethod)
		
        val userDefinedParams =
            params.parse(
                request,
                "response_type",
                "client_id",
                "redirect_uri",
                "state",
                "scope",
                "code_challenge",
                "code_challenge_method",
            )
        val scopes: Set<String> = Sets.newTreeSet(Splitter.on(" ").omitEmptyStrings().split(scope))
		
        val result =
            clientAuthorizer.authorizeClient(
                AuthorizationRequest(
                    clientId,
                    subjectId,
                    responseType,
                    scopes,
                    codeChallenge,
                    userDefinedParams,
                    time = instantTime.toLocalDateTime(),
                ),
            )
		
        when (result) {
            is ClientAuthorizationResult.Success -> {
                val possibleRedirectUrl = result.client.determineRedirectUrl(requestedUrl)
                if (!possibleRedirectUrl.isPresent) {
                    return OAuthError.unauthorizedClient("Client Redirect URL is not matching the configured one.")
                }
                val callbackUrl =
                    Urlsome(possibleRedirectUrl.get())[
                        "code" to result.authCode,
                        "state" to state,
                    ].toString()
                return RsRedirect(callbackUrl)
            }
			
            is ClientAuthorizationResult.ClientNotFound -> {
                // The identity provider is not able to determine the client,
                // so we tell the caller that it's an unknown or miss-configured.
                return OAuthError.unauthorizedClient("Unknown client '$clientId'")
            }
            is ClientAuthorizationResult.AccessDenied -> {
                // RFC-6749 - Section: 4.2.2.1
                // The authorization server redirects the user-agent by
                // sending the following HTTP response:
                // HTTP/1.1 302 Found
                // Location: https://client.example.com/cb#error=access_denied&state=xyz
                val callbackUrl =
                    Urlsome(requestedUrl)[
                        "error" to "access_denied",
                        "reason" to result.reason,
                        "state" to state,
                    ].toString()
                return RsRedirect(callbackUrl)
            }
            is ClientAuthorizationResult.Error -> {
                val callbackUrl =
                    Urlsome(requestedUrl)[
                        "error" to "internal_error",
                        "message" to result.message,
                    ].toString()
                return RsRedirect(callbackUrl)
            }
        }
    }
}
