package com.clouway.oauth2

import com.clouway.friendlyserve.testing.ParamRequest
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.client.ClientCredentials
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.BearerToken
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdTokenBuilder
import com.clouway.oauth2.token.Identity
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.TokenResponse
import com.clouway.oauth2.token.Tokens
import com.google.common.base.Optional
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HandleTokenExchangeRequestsTest {
    @get:Rule val mockkRule = MockKRule(this)

    @MockK lateinit var tokens: Tokens
    @MockK lateinit var identityFinder: IdentityFinder
    @MockK lateinit var idTokenFactory: IdTokenFactory

    private lateinit var controller: TokenExchangeController

    @Before
    fun setUp() {
        controller = TokenExchangeController(tokens, identityFinder, idTokenFactory)
    }

    @Test
    fun exchangesAccessTokenForAccessAndIdToken() {
        val now = DateTime()
        val subject = BearerToken(
            "sub123",
            GrantType.AUTHORIZATION_CODE,
            "user-1",
            "client-a",
            "user@example.com",
            setOf("read", "write"),
            now.plusSeconds(3600),
            mapOf("subject_kind" to "user"),
        )

        every { tokens.findTokenAvailableAt("subtoken", any()) } returns Optional.of(subject)
        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns FindIdentityResult.User(
            Identity("user-1", "User One", "User", "One", "user@example.com", null, emptyMap()),
        )

        every { tokens.issueToken(any()) } returns TokenResponse(true,
            BearerToken("new-access", GrantType.TOKEN_EXCHANGE, "user-1", "caller", "user@example.com", setOf("read"), now.plusSeconds(3600), mapOf()),
            "new-refresh")

        val builder = io.mockk.mockk<IdTokenBuilder>()
        every { idTokenFactory.newBuilder() } returns builder
        every { builder.issuer(any()) } returns builder
        every { builder.audience(any()) } returns builder
        every { builder.subjectUser(any()) } returns builder
        every { builder.subjectServiceAccount(any()) } returns builder
        every { builder.ttl(any()) } returns builder
        every { builder.issuedAt(any()) } returns builder
        every { builder.withAccessToken(any()) } returns builder
        every { builder.build() } returns "idtok"

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "subtoken",
                "subject_token_type" to "urn:ietf:params:oauth:token-type:access_token",
                "requested_token_type" to "urn:ietf:params:oauth:token-type:access_token urn:ietf:params:oauth:token-type:id_token",
                "scope" to "read",
                "Authorization" to basic("caller","secret"),
            ),
        )

        val res = controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        val body = com.clouway.friendlyserve.testing.RsPrint(res).printBody()
        assertThat(body, containsString("\"access_token\":\"new-access\""))
        assertThat(body, containsString("\"id_token\":\"idtok\""))
        assertThat(body, containsString("\"issued_token_type\":\"urn:ietf:params:oauth:token-type:access_token\""))
    }

    @Test
    fun rejectsScopeNotSubset() {
        val now = DateTime()
        val subject = BearerToken("sub", GrantType.AUTHORIZATION_CODE, "user-1", "client-a", "user@example.com", setOf("read"), now.plusSeconds(3600), mapOf())
        every { tokens.findTokenAvailableAt("subtoken", any()) } returns Optional.of(subject)

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "subtoken",
                "scope" to "write",
                "Authorization" to basic("caller","secret"),
            ),
        )

        val res = controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        val body = com.clouway.friendlyserve.testing.RsPrint(res).printBody()
        assertThat(body, containsString("invalid_scope"))
    }

    @Test
    fun exchangesServiceAccountTokenWithRefreshRequested() {
        val now = DateTime()
        val subject = BearerToken(
            "subsvc",
            GrantType.JWT,
            "svc-1",
            "client-a",
            "svc@example.com",
            setOf("svc.read"),
            now.plusSeconds(3600),
            mapOf("subject_kind" to "service_account"),
        )

        every { tokens.findTokenAvailableAt("subtoken", any()) } returns Optional.of(subject)
        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns FindIdentityResult.ServiceAccountClient(
            com.clouway.oauth2.token.ServiceAccount(clientId = "svc-1", clientEmail = "svc@example.com", name = "Service A", customerId = null, claims = emptyMap()),
        )

        every { tokens.issueToken(any()) } returns TokenResponse(true,
            BearerToken("new-access", GrantType.TOKEN_EXCHANGE, "svc-1", "caller", "svc@example.com", setOf("svc.read"), now.plusSeconds(3600), mapOf()),
            "new-refresh")

        val builder2 = io.mockk.mockk<IdTokenBuilder>()
        every { idTokenFactory.newBuilder() } returns builder2
        every { builder2.issuer(any()) } returns builder2
        every { builder2.audience(any()) } returns builder2
        every { builder2.subjectServiceAccount(any()) } returns builder2
        every { builder2.ttl(any()) } returns builder2
        every { builder2.issuedAt(any()) } returns builder2
        every { builder2.withAccessToken(any()) } returns builder2
        every { builder2.build() } returns "idtok-svc"

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "subtoken",
                "subject_token_type" to "urn:ietf:params:oauth:token-type:access_token",
                "requested_token_type" to "urn:ietf:params:oauth:token-type:access_token urn:ietf:params:oauth:token-type:id_token urn:ietf:params:oauth:token-type:refresh_token",
                "scope" to "svc.read",
                "Authorization" to basic("caller","secret"),
            ),
        )

        val res = controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        val body = com.clouway.friendlyserve.testing.RsPrint(res).printBody()
        assertThat(body, containsString("\"access_token\":\"new-access\""))
        assertThat(body, containsString("\"id_token\":\"idtok-svc\""))
        assertThat(body, containsString("\"refresh_token\":\"new-refresh\""))
    }

    @Test
    fun rejectsUnsupportedRequestedTokenType() {
        val now = DateTime()
        val subject = BearerToken("sub", GrantType.AUTHORIZATION_CODE, "user-1", "client-a", "user@example.com", setOf("read"), now.plusSeconds(3600), mapOf())
        every { tokens.findTokenAvailableAt("subtoken", any()) } returns Optional.of(subject)

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "subtoken",
                "requested_token_type" to "urn:ietf:params:oauth:token-type:unknown",
                "Authorization" to basic("caller","secret"),
            ),
        )

        val res = controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        val body = com.clouway.friendlyserve.testing.RsPrint(res).printBody()
        assertThat(body, containsString("invalid_request"))
    }

    @Test
    fun rejectsInvalidSubjectToken() {
        val now = DateTime()
        every { tokens.findTokenAvailableAt("bad", any()) } returns Optional.absent()

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "bad",
                "Authorization" to basic("caller","secret"),
            ),
        )

        val res = controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        val body = com.clouway.friendlyserve.testing.RsPrint(res).printBody()
        assertThat(body, containsString("invalid_token"))
    }

    @Test
    fun audienceIsPropagatedToTokenRequest() {
        val now = DateTime()
        val subject = BearerToken("sub", GrantType.AUTHORIZATION_CODE, "user-1", "client-a", "user@example.com", setOf("read"), now.plusSeconds(3600), mapOf())
        every { tokens.findTokenAvailableAt("subtoken", any()) } returns Optional.of(subject)
        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns FindIdentityResult.User(
            Identity("user-1", "User One", "User", "One", "user@example.com", null, emptyMap()),
        )

        var capturedParams: Map<String, String>? = null
        every { tokens.issueToken(any()) } answers {
            val tr = firstArg<com.clouway.oauth2.token.TokenRequest>()
            capturedParams = tr.params
            TokenResponse(true,
                BearerToken("new-access", GrantType.TOKEN_EXCHANGE, "user-1", "caller", "user@example.com", setOf("read"), now.plusSeconds(3600), mapOf()),
                null)
        }
        val builder3 = io.mockk.mockk<IdTokenBuilder>()
        every { idTokenFactory.newBuilder() } returns builder3
        every { builder3.issuer(any()) } returns builder3
        every { builder3.audience(any()) } returns builder3
        every { builder3.subjectUser(any()) } returns builder3
        every { builder3.subjectServiceAccount(any()) } returns builder3
        every { builder3.ttl(any()) } returns builder3
        every { builder3.issuedAt(any()) } returns builder3
        every { builder3.withAccessToken(any()) } returns builder3
        // id_token not required in this scenario; still return a token to avoid exceptions
        every { builder3.build() } returns "idtok"

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "subtoken",
                "audience" to "service-b",
                "Authorization" to basic("caller","secret"),
            ),
        )

        controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        assertThat(capturedParams!!["audience"], equalTo("service-b"))
    }

    @Test
    fun inheritsSubjectScopesWhenScopeOmitted() {
        val now = DateTime()
        val subjectScopes = setOf("read", "write")
        val subject = BearerToken("sub", GrantType.AUTHORIZATION_CODE, "user-1", "client-a", "user@example.com", subjectScopes, now.plusSeconds(3600), mapOf())
        every { tokens.findTokenAvailableAt("subtoken", any()) } returns Optional.of(subject)
        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns FindIdentityResult.User(
            Identity("user-1", "User One", "User", "One", "user@example.com", null, emptyMap()),
        )
        every { tokens.issueToken(any()) } answers {
            val tr = firstArg<com.clouway.oauth2.token.TokenRequest>()
            // ensure scopes inherited
            assertThat(tr.scopes, equalTo(subjectScopes))
            TokenResponse(true,
                BearerToken("new-access", GrantType.TOKEN_EXCHANGE, "user-1", "caller", "user@example.com", tr.scopes, now.plusSeconds(3600), mapOf()),
                null)
        }
        val builder4 = io.mockk.mockk<IdTokenBuilder>()
        every { idTokenFactory.newBuilder() } returns builder4
        every { builder4.issuer(any()) } returns builder4
        every { builder4.audience(any()) } returns builder4
        every { builder4.subjectUser(any()) } returns builder4
        every { builder4.ttl(any()) } returns builder4
        every { builder4.issuedAt(any()) } returns builder4
        every { builder4.withAccessToken(any()) } returns builder4
        // id_token not requested; builder can return any string
        every { builder4.build() } returns "idtok"

        val req = ParamRequest(
            mapOf(
                "grant_type" to "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token" to "subtoken",
                "Authorization" to basic("caller","secret"),
            ),
        )

        val res = controller.handleAsOf(req, ClientCredentials("caller","secret"), now)
        val body = com.clouway.friendlyserve.testing.RsPrint(res).printBody()
        assertThat(body, containsString("\"scope\":\"read write\""))
    }

    private fun basic(id: String, secret: String): String {
        val creds = java.util.Base64.getEncoder().encodeToString("$id:$secret".toByteArray())
        return "Basic $creds"
    }
}
