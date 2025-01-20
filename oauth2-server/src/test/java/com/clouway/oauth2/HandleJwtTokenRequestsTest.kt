package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.testing.FakeRequest
import com.clouway.friendlyserve.testing.RsPrint
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.client.JwtKeyStore
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.jws.Pem
import com.clouway.oauth2.jws.Signature
import com.clouway.oauth2.jws.SignatureFactory
import com.clouway.oauth2.jwt.Jwt
import com.clouway.oauth2.token.BearerTokenBuilder
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.IdTokenFactory
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.Permissions
import com.clouway.oauth2.token.Scope
import com.clouway.oauth2.token.ServiceAccount
import com.clouway.oauth2.token.TokenRequest
import com.clouway.oauth2.token.TokenResponse
import com.clouway.oauth2.token.Tokens
import com.google.common.base.Optional
import com.google.common.collect.ImmutableMap
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class HandleJwtTokenRequestsTest {
    @JvmField
    @Rule
    var context: JUnitRuleMockery = JUnitRuleMockery()

    @get:Rule
    val mockkRule = MockKRule(this)

    private val body =
        "eyJpc3MiOiJ4eHhAZGV2ZWxvcGVyLmNvbSIsInNjb3BlIjoidGVzdDEgdGVzdDIiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjkwMDIvb2F1dGgyL3Rva2VuIiwiZXhwIjoxNDYxMjM4OTQ4LCJpYXQiOjE0NjEyMzUzNDgsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJwcm4iOiJ1c2VyQGV4YW1wbGUuY29tIn0"
    private val signature =
        "WBAzzss3J8Ea6-xxOCVS2OZ2HoqpiLdfCLhIJEevaPck377qTpiM__lHta_S8dSCuTl5FjREqixIiwGrJVJEIkfExUwS5YWekdJRniSKdqLjmXussePaCSgco3reJDqNcRCGiv9DSLH0GfZFdv11Ik5nyaHjNnS4ykEi76guaY8-T3uVFjOH4e2o8Wm0vBbq9hzo9UHdgnsI2BLrzDVoydGWM7uZW8MQNKTuGWY_Ywyj1hilr9rw4yy2FvBe7G-56qaq8--IlVNZ6ocJX2dYhZPqDtZUYwLRqwFyM_F53Kt81I8Qht6HBgH-fgrfbd7Ms67BeLGsupFvuM9sF-hGOQ"

    @MockK
    lateinit var repository: JwtKeyStore

    @MockK
    lateinit var tokens: Tokens

    @MockK
    lateinit var signatureFactory: SignatureFactory

    @MockK
    lateinit var identityFinder: IdentityFinder

    @MockK
    lateinit var idTokenFactory: IdTokenFactory
	
    private lateinit var controller: JwtController

    @Before
    fun setUp() {
        controller =
            JwtController(
                signatureFactory,
                tokens,
                repository,
                identityFinder,
                idTokenFactory,
            )
    }

    @Test
    @Throws(IOException::class)
    fun happyPath() {
        val anySignatureThatWillVerifies =
            mockk<Signature>()

        val anyInstantTime = DateTime()
        every { repository.findKey(any<Jwt.Header>(), any<Jwt.ClaimSet>()) } returns
            Optional.of(Pem.Block("", ImmutableMap.of(), byteArrayOf()))
        every { signatureFactory.createSignature(any(), any()) } returns
            Optional.of(anySignatureThatWillVerifies)
        every { anySignatureThatWillVerifies.verifyWithPrivateKey(any(), any()) } returns true

        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns
            FindIdentityResult.ServiceAccountClient(
                ServiceAccount(clientId = "::id::", clientEmail = "::email::", name = "::name::", customerId = null, claims = emptyMap()),
            )
        every { idTokenFactory.create(any(), any(), any<ServiceAccount>(), any(), any()) } returns
            Optional.of("::id_token::")
        every { tokens.issueToken(any<TokenRequest>()) } returns
            TokenResponse(
                true,
                BearerTokenBuilder
                    .aNewToken()
                    .withValue("::access_token::")
                    .expiresAt(anyInstantTime.plusSeconds(1000))
                    .build(),
                "::refresh_token::",
            )

        val response =
            controller.handleAsOf(
                newJwtRequest(
                    String.format(
                        "%s.%s.%s",
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                        body,
                        signature,
                    ),
                    "::host::",
                ),
                anyInstantTime,
            )
        val responseContent = RsPrint(response).printBody()
        Assert.assertThat(responseContent, Matchers.containsString("::access_token::"))
        Assert.assertThat(responseContent, Matchers.containsString("::refresh_token::"))
        Assert.assertThat(responseContent, Matchers.containsString("1000"))
        Assert.assertThat(responseContent, Matchers.containsString("::id_token::"))
    }

    @Test
    @Throws(Exception::class)
    fun idTokenWasNotGenerated() {
        val anySignatureThatWillVerifies =
            mockk<Signature>()

        val anyInstantTime = DateTime()
        every { repository.findKey(any<Jwt.Header>(), any<Jwt.ClaimSet>()) } returns
            Optional.of(Pem.Block("", ImmutableMap.of(), byteArrayOf()))
        every { signatureFactory.createSignature(any(), any()) } returns
            Optional.of(anySignatureThatWillVerifies)
        every { anySignatureThatWillVerifies.verifyWithPrivateKey(any(), any()) } returns true

        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns
            FindIdentityResult.ServiceAccountClient(
                ServiceAccount(clientId = "::id::", clientEmail = "::email::", name = "::name::", customerId = null, claims = emptyMap()),
            )
        every { idTokenFactory.create(any(), any(), any<ServiceAccount>(), any(), any()) } returns
            Optional.absent()

        every { tokens.issueToken(any<TokenRequest>()) } returns
            TokenResponse(
                true,
                BearerTokenBuilder
                    .aNewToken()
                    .withValue("::access_token::")
                    .expiresAt(anyInstantTime.plusSeconds(1000))
                    .build(),
                "::refresh_token::",
            )
		
        val response =
            controller.handleAsOf(
                newJwtRequest(
                    String.format(
                        "%s.%s.%s",
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                        body,
                        signature,
                    ),
                    "::host::",
                ),
                anyInstantTime,
            )
        val responseContent = RsPrint(response).printBody()
        Assert.assertThat(responseContent, Matchers.containsString("::access_token::"))
        Assert.assertThat(responseContent, Matchers.containsString("::refresh_token::"))
        Assert.assertThat(responseContent, Matchers.containsString("1000"))
        Assert.assertFalse(responseContent.contains("id_token"))
    }

    @Test
    @Throws(Exception::class)
    fun serviceAccountWasNotFound() {
        val anySignatureThatWillVerifies =
            mockk<Signature>()

        val anyInstantTime = DateTime()
        every { repository.findKey(any<Jwt.Header>(), any<Jwt.ClaimSet>()) } returns
            Optional.of(Pem.Block("", ImmutableMap.of(), byteArrayOf()))
        every { signatureFactory.createSignature(any(), any()) } returns
            Optional.of(anySignatureThatWillVerifies)
        every { anySignatureThatWillVerifies.verifyWithPrivateKey(any(), any()) } returns true

        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns
            FindIdentityResult.NotFound

        val response =
            controller.handleAsOf(
                newJwtRequest(
                    String.format(
                        "%s.%s.%s",
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                        body,
                        signature,
                    ),
                    "::host::",
                ),
                anyInstantTime,
            )
        val responseContent = RsPrint(response).printBody()
        Assert.assertThat(responseContent, Matchers.containsString("unknown identity"))
    }

    @Test
    @Throws(IOException::class)
    fun scopesArePassed() {
        val jwtClient = Client("xxx@developer.com", "", "", emptySet(), false)
        val serviceAccount = ServiceAccount("xxx@developer.com", "::email::", "::name::", null, emptyMap())

        val anySignatureThatWillVerifies =
            mockk<Signature>()

        val anyInstantTime = DateTime()
        every { repository.findKey(any<Jwt.Header>(), any<Jwt.ClaimSet>()) } returns
            Optional.of(Pem.Block("", ImmutableMap.of(), byteArrayOf()))
        every { signatureFactory.createSignature(any(), any()) } returns
            Optional.of(anySignatureThatWillVerifies)
        every { anySignatureThatWillVerifies.verifyWithPrivateKey(any(), any()) } returns true

        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns
            FindIdentityResult.ServiceAccountClient(serviceAccount)
        every { idTokenFactory.create(any(), any(), any<ServiceAccount>(), any(), any()) } returns
            Optional.of("::id_token::")
        every {
            tokens.issueToken(
                TokenRequest(
                    grantType = GrantType.JWT,
                    client = jwtClient,
                    serviceAccount = serviceAccount,
                    scopes = sortedSetOf("CanDoX", "CanDoY", "test1", "test2"),
                    `when` = anyInstantTime,
                    params = mapOf(),
                ),
            )
        } returns
            TokenResponse(
                true,
                BearerTokenBuilder
                    .aNewToken()
                    .withValue("::access_token::")
                    .expiresAt(anyInstantTime.plusSeconds(1000))
                    .build(),
                "::refresh_token::",
            )
		
        controller.handleAsOf(
            newJwtRequest(
                String.format(
                    "%s.%s.%s",
                    "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                    body,
                    signature,
                ),
                "CanDoX CanDoY",
                "::host::",
            ),
            anyInstantTime,
        )
    }

    @Test
    @Throws(IOException::class)
    fun requestedScopeWasNotAllowed() {
        val jwtClient = Client("xxx@developer.com", "", "", emptySet(), false)
        val serviceAccount =
            ServiceAccount(
                clientId = "xxx@developer.com",
                clientEmail = "::email::",
                name = "::name::",
                customerId = null,
                claims = emptyMap(),
                permissions = Permissions(allowAll = false, scopes = listOf(Scope("CanDoX"), Scope("CanDoY"))),
            )

        val anySignatureThatWillVerifies =
            mockk<Signature>()

        val anyInstantTime = DateTime()
        every { repository.findKey(any<Jwt.Header>(), any<Jwt.ClaimSet>()) } returns
            Optional.of(Pem.Block("", ImmutableMap.of(), byteArrayOf()))
        every { signatureFactory.createSignature(any(), any()) } returns
            Optional.of(anySignatureThatWillVerifies)
        every { anySignatureThatWillVerifies.verifyWithPrivateKey(any(), any()) } returns true

        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns
            FindIdentityResult.ServiceAccountClient(serviceAccount)

        val response =
            controller.handleAsOf(
                newJwtRequest(
                    String.format(
                        "%s.%s.%s",
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                        body,
                        signature,
                    ),
                    "CanDoX CanDoY CanDoZ",
                    "::host::",
                ),
                anyInstantTime,
            )
        assertThat(RsPrint(response).print(), Matchers.containsString("invalid_request"))
    }

    @Test
    @Throws(IOException::class)
    fun customRequestParamsArePassed() {
        val jwtClient = Client("xxx@developer.com", "", "", emptySet(), false)
        val serviceAccount = ServiceAccount("xxx@developer.com", "::email::", "::name::", null, emptyMap())

        val anySignatureThatWillVerifies =
            mockk<Signature>()

        val anyInstantTime = DateTime()
        every { repository.findKey(any<Jwt.Header>(), any<Jwt.ClaimSet>()) } returns
            Optional.of(Pem.Block("", ImmutableMap.of(), byteArrayOf()))
        every { signatureFactory.createSignature(any(), any()) } returns
            Optional.of(anySignatureThatWillVerifies)
        every { anySignatureThatWillVerifies.verifyWithPrivateKey(any(), any()) } returns true

        every { identityFinder.findIdentity(any<FindIdentityRequest>()) } returns
            FindIdentityResult.ServiceAccountClient(serviceAccount)
        every { idTokenFactory.create(any(), any(), any<ServiceAccount>(), any(), any()) } returns
            Optional.of("::id_token::")
        every {
            tokens.issueToken(
                TokenRequest(
                    grantType = GrantType.JWT,
                    client = jwtClient,
                    serviceAccount = serviceAccount,
                    scopes = setOf("CanDoX", "CanDoY", "test1", "test2"),
                    `when` = anyInstantTime,
                    params = mapOf("::index::" to "::1::"),
                ),
            )
        } returns
            TokenResponse(
                true,
                BearerTokenBuilder
                    .aNewToken()
                    .withValue("::access_token::")
                    .expiresAt(anyInstantTime.plusSeconds(1000))
                    .build(),
                "::refresh_token::",
            )

        controller.handleAsOf(
            FakeRequest
                .aNewRequest()
                .params(
                    ImmutableMap.of(
                        "assertion",
                        String.format("%s.%s.%s", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9", body, signature),
                        "scope",
                        "CanDoX CanDoY",
                        "::index::",
                        "::1::",
                    ),
                ).header("Host", "::host::")
                .build(),
            anyInstantTime,
        )
    }

    @Test
    @Throws(IOException::class)
    fun assertionIsEmpty() {
        val response = controller.handleAsOf(newJwtRequest("", "::host::"), DateTime())
        assertThat(RsPrint(response).print(), Matchers.containsString("invalid_request"))
    }

    @Test
    @Throws(IOException::class)
    fun headerIsMissing() {
        val response =
            controller.handleAsOf(newJwtRequest(String.format("%s.%s", body, signature), "::host::"), DateTime())
        assertThat(RsPrint(response).printBody(), Matchers.containsString("invalid_request"))
    }

    private fun newJwtRequest(
        assertion: String,
        hostHeader: String,
    ): Request =
        FakeRequest
            .aNewRequest()
            .param("assertion", assertion)
            .header("Host", hostHeader)
            .build()

    private fun newJwtRequest(
        assertion: String,
        scope: String,
        hostHeader: String,
    ): Request =
        FakeRequest
            .aNewRequest()
            .param("assertion", assertion)
            .param("scope", scope)
            .header("Host", hostHeader)
            .build()
}
