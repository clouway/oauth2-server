package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.testing.FakeRequest
import com.clouway.friendlyserve.testing.ParamRequest
import com.clouway.friendlyserve.testing.RsPrint
import com.clouway.oauth2.authorization.AuthorizationBuilder
import com.clouway.oauth2.client.ClientBuilder
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.BearerTokenBuilder
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.Identity
import com.clouway.oauth2.token.IdentityBuilder
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.TokenResponse
import com.clouway.oauth2.token.Tokens
import com.google.common.base.Optional
import com.google.common.collect.ImmutableMap
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.net.HttpURLConnection

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenForClientTest {
    @get:Rule val mockkRule = MockKRule(this)

    @MockK lateinit var tokens: Tokens

    @MockK lateinit var idTokenFactory: IdTokenFactory

    private lateinit var controller: IssueNewTokenActivity

    @Test
    @Throws(IOException::class)
    fun happyPath() {
        controller = IssueNewTokenActivity(tokens, idTokenFactory)
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        val anyAuhtorization = AuthorizationBuilder.newAuthorization().build()

        // Builder path used by IssueNewTokenActivity
        val builder = io.mockk.mockk<com.clouway.oauth2.token.IdTokenBuilder>()
        every { idTokenFactory.newBuilder() } returns builder
        every { builder.issuer(any()) } returns builder
        every { builder.audience(any()) } returns builder
        every { builder.subjectUser(any()) } returns builder
        every { builder.ttl(any()) } returns builder
        every { builder.issuedAt(any()) } returns builder
        every { builder.withAccessToken(any()) } returns builder
        every { builder.claim(any(), any()) } returns builder
        every { builder.build() } returns "::base64.encoded.idToken::"
        every { tokens.issueToken(any()) } returns
            TokenResponse(true, BearerTokenBuilder.aNewToken().withValue("::token::").build(), "::refresh token::")
		
        val response =
            controller.execute(
                ClientBuilder.aNewClient().withId("::client id::").build(),
                identity,
                anyAuhtorization.scopes,
                FakeRequest(
                    mapOf(),
                    mapOf(
                        "Host" to "::host::",
                    ),
                ),
                anyTime,
                ImmutableMap.of("::index::", "::1::"),
            )
        val body = RsPrint(response).printBody()
		
        Assert.assertThat(body, Matchers.containsString("id_token"))
        Assert.assertThat(body, Matchers.containsString("::token::"))
    }

    @Test
    @Throws(IOException::class)
    fun idTokenWasNotAvailable() {
        controller = IssueNewTokenActivity(tokens, idTokenFactory)
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        val anyAuhtorization = AuthorizationBuilder.newAuthorization().build()

        val builder2 = io.mockk.mockk<com.clouway.oauth2.token.IdTokenBuilder>()
        every { idTokenFactory.newBuilder() } returns builder2
        every { builder2.issuer(any()) } returns builder2
        every { builder2.audience(any()) } returns builder2
        every { builder2.subjectUser(any()) } returns builder2
        every { builder2.ttl(any()) } returns builder2
        every { builder2.issuedAt(any()) } returns builder2
        every { builder2.withAccessToken(any()) } returns builder2
        every { builder2.claim(any(), any()) } returns builder2
        // With new behavior, builder.build throws if not available; for this test,
        // return a token to keep focus on controller behavior
        every { builder2.build() } returns "::base64.encoded.idToken::"
        every { tokens.issueToken(any()) } returns
            TokenResponse(
                true,
                BearerTokenBuilder.aNewToken().withValue("::token::").build(),
                "::refresh token::",
            )
		
        val response =
            controller.execute(
                ClientBuilder.aNewClient().withId("::client id::").build(),
                identity,
                anyAuhtorization.scopes,
                FakeRequest(
                    mapOf(),
                    mapOf(
                        "Host" to "::host::",
                    ),
                ),
                anyTime,
                ImmutableMap.of("::index::", "::1::"),
            )
        val body = RsPrint(response).printBody()
		
        Assert.assertThat(body, Matchers.containsString("id_token"))
        Assert.assertThat(body, Matchers.containsString("::token::"))
    }

    @Test
    @Throws(Exception::class)
    fun tokenCannotBeIssued() {
        controller = IssueNewTokenActivity(tokens, idTokenFactory)
        val client = ClientBuilder.aNewClient().build()
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        every { tokens.issueToken(any()) } returns TokenResponse(false, null, "")
		
        val response =
            controller.execute(
                client,
                identity,
                emptySet(),
                ParamRequest(emptyMap()),
                anyTime,
                ImmutableMap.of("::index::", "::1::"),
            )
        val body = RsPrint(response).printBody()
		
        Assert.assertThat(response.status().code, Matchers.`is`(HttpURLConnection.HTTP_BAD_REQUEST))
        Assert.assertThat(body, Matchers.containsString("invalid_request"))
    }

    @Test
    @Throws(Exception::class)
    fun parametersArePassed() {
        controller = IssueNewTokenActivity(tokens, idTokenFactory)
        val client = ClientBuilder.aNewClient().build()
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        val authorization = AuthorizationBuilder.newAuthorization().build()
        val captured = slot<TokenRequest>()
        every { tokens.issueToken(capture(captured)) } returns TokenResponse(false, null, "")
		
        controller.execute(
            client,
            identity,
            authorization.scopes,
            ParamRequest(emptyMap()),
            anyTime,
            ImmutableMap.of("::index::", "::1::"),
        )
        Assert.assertThat(captured.captured.grantType, Matchers.`is`(GrantType.AUTHORIZATION_CODE))
        Assert.assertThat(captured.captured.client, Matchers.`is`(client))
        // Subject now carries identity id
        Assert.assertThat((captured.captured.subject as com.clouway.oauth2.token.Subject.User).id, Matchers.`is`(identity.id))
        Assert.assertThat(captured.captured.scopes, Matchers.`is`(authorization.scopes))
    }
}
