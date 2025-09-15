package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.Identity
import com.clouway.oauth2.token.Subject
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.Tokens

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity(
    private val tokens: Tokens,
    private val idTokenFactory: IdTokenFactory,
) : AuthorizedIdentityActivity {
    override fun execute(
        client: Client,
        identity: Identity,
        scopes: Set<String>,
        request: Request,
        instant: DateTime,
        params: Map<String, String>,
    ): Response {
        val response =
            tokens.issueToken(
                TokenRequest(
                    grantType = GrantType.AUTHORIZATION_CODE,
                    client = client,
                    subject = Subject.User(identity.id),
                    scopes = scopes,
                    `when` = instant,
                    claims = identity.claims,
                    params = params,
                ),
            )
		
        if (!response.isSuccessful) {
            return OAuthError.invalidRequest("Token cannot be issued.")
        }

        // The issuer can be configured through environment variable IDP_CONFIG_JWT_ISSUER.
        // If not set, the issuer is constructed from the request (scheme and host).
        val issuer = LocalConfig.jwtIssuerName() ?: buildIssuerFromRequest(request)

        // Prefer the standard resource parameter to indicate which API was requested by the client. If not
        // present, fall back to clientId for backward compatibility.
        // See https://tools.ietf.org/html/rfc8707
        var audience = if (params.contains("resource")) params.getValue("resource") else request.param("resource")
        if (audience.isNullOrEmpty()) {
            audience = client.id
        }

        val accessToken = response.accessToken
        val idToken =
            idTokenFactory
                .newBuilder()
                .issuer(issuer)
                .audience(audience)
                .subjectUser(identity)
                .ttl(accessToken.ttlSeconds(instant))
                .issuedAt(instant)
                .withAccessToken(accessToken.value)
                .build()
		
        // Compute at_hash when id_token is present and access_token is issued with it
        return BearerTokenResponse(
            accessToken.value,
            accessToken.ttlSeconds(instant),
            accessToken.scopes,
            response.refreshToken,
            idToken,
        )
    }

    private fun buildIssuerFromRequest(request: Request): String {
        // Try RFC7239 Forwarded header first (e.g. "for=1.2.3.4; proto=https; host=example.com")
        val forwarded = request.header("Forwarded") ?: request.header("forwarded")
        var scheme: String? = null
        var host: String? = null

        if (!forwarded.isNullOrEmpty()) {
            val first = forwarded.split(",").first().trim()
            first
                .split(";")
                .map { it.trim() }
                .forEach { pair ->
                    val idx = pair.indexOf('=')
                    if (idx > 0) {
                        val name = pair.substring(0, idx).trim().lowercase()
                        var value = pair.substring(idx + 1).trim()
                        if (value.startsWith("\"") && value.endsWith("\"") && value.length >= 2) {
                            value = value.substring(1, value.length - 1)
                        }
                        when (name) {
                            "proto" -> scheme = value
                            "host" -> host = value
                        }
                    }
                }
        }

        // X-Forwarded-* fallbacks
        if (scheme.isNullOrEmpty()) {
            val xProto = request.header("X-Forwarded-Proto") ?: request.header("x-forwarded-proto")
            if (!xProto.isNullOrEmpty()) {
                scheme = xProto.split(",").first().trim()
            } else {
                val hdrScheme = request.header("scheme")
                if (!hdrScheme.isNullOrEmpty()) {
                    scheme = hdrScheme.trim()
                }
            }
        }

        if (host.isNullOrEmpty()) {
            val xHost = request.header("X-Forwarded-Host") ?: request.header("x-forwarded-host")
            if (!xHost.isNullOrEmpty()) {
                host = xHost.split(",").first().trim()
            } else {
                val hostHeader = request.header("Host") ?: request.header("host")
                if (!hostHeader.isNullOrEmpty()) {
                    host = hostHeader.trim()
                }
            }
        }

        // If host has no port but X-Forwarded-Port is present, append it when appropriate
        val hostSnapshot = host
        if (!hostSnapshot.isNullOrEmpty() && !hostSnapshot.contains(":")) {
            val xPort = request.header("X-Forwarded-Port") ?: request.header("x-forwarded-port")
            val port = xPort?.split(",")?.firstOrNull()?.trim()
            if (!port.isNullOrEmpty()) {
                val portInt = port.toIntOrNull()
                if (portInt != null) {
                    val usePort =
                        when (scheme) {
                            "http" -> portInt != 80
                            "https" -> portInt != 443
                            else -> true
                        }
                    if (usePort) {
                        host = "$hostSnapshot:$portInt"
                    }
                } else {
                    host = "$hostSnapshot:$port"
                }
            }
        }

        val finalScheme = if (!scheme.isNullOrEmpty()) scheme else "https"
        val finalHost = if (!host.isNullOrEmpty()) host else (request.header("Host") ?: "localhost")

        return "$finalScheme://$finalHost"
    }
}
