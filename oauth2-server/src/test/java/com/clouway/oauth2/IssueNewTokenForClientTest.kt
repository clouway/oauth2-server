package com.clouway.oauth2

import com.clouway.friendlyserve.Request
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
import org.hamcrest.Matchers
import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.net.HttpURLConnection

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenForClientTest {
    @JvmField
    @Rule
    var context: JUnitRuleMockery = JUnitRuleMockery()
	
    private val tokens: Tokens = context.mock(Tokens::class.java)
	
    private val idTokenFactory: IdTokenFactory =
        context.mock(
            IdTokenFactory::class.java,
        )
	
    private val request: Request = context.mock(Request::class.java)
	
    private val controller = IssueNewTokenActivity(tokens, idTokenFactory)

    @Test
    @Throws(IOException::class)
    fun happyPath() {
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        val anyAuhtorization = AuthorizationBuilder.newAuthorization().build()
		
        context.checking(
            object : Expectations() {
                init {
                    oneOf(request).header("Host")
                    will(returnValue("::host::"))
				
                    oneOf(idTokenFactory).create(
                        with(
                            any(
                                String::class.java,
                            ),
                        ),
                        with(any(String::class.java)),
                        with(
                            any(
                                Identity::class.java,
                            ),
                        ),
                        with(any(Long::class.java)),
                        with(
                            any(
                                DateTime::class.java,
                            ),
                        ),
                    )
                    will(returnValue(Optional.of("::base64.encoded.idToken::")))
				
                    oneOf(tokens).issueToken(
                        with(
                            any(
                                TokenRequest::class.java,
                            ),
                        ),
                    )
                    will(
                        returnValue(
                            TokenResponse(
                                true,
                                BearerTokenBuilder.aNewToken().withValue("::token::").build(),
                                "::refresh token::",
                            ),
                        ),
                    )
                }
            },
        )
		
        val response =
            controller.execute(
                ClientBuilder.aNewClient().withId("::client id::").build(),
                identity,
                anyAuhtorization.scopes,
                request,
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
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        val anyAuhtorization = AuthorizationBuilder.newAuthorization().build()
		
        context.checking(
            object : Expectations() {
                init {
                    oneOf(request).header("Host")
                    will(returnValue("::host::"))
				
                    oneOf(idTokenFactory).create(
                        with(
                            any(
                                String::class.java,
                            ),
                        ),
                        with(any(String::class.java)),
                        with(
                            any(
                                Identity::class.java,
                            ),
                        ),
                        with(any(Long::class.java)),
                        with(
                            any(
                                DateTime::class.java,
                            ),
                        ),
                    )
                    will(returnValue(Optional.absent<Any>()))
				
                    oneOf(tokens).issueToken(
                        with(
                            any(
                                TokenRequest::class.java,
                            ),
                        ),
                    )
                    will(
                        returnValue(
                            TokenResponse(
                                true,
                                BearerTokenBuilder.aNewToken().withValue("::token::").build(),
                                "::refresh token::",
                            ),
                        ),
                    )
                }
            },
        )
		
        val response =
            controller.execute(
                ClientBuilder.aNewClient().withId("::client id::").build(),
                identity,
                anyAuhtorization.scopes,
                request,
                anyTime,
                ImmutableMap.of("::index::", "::1::"),
            )
        val body = RsPrint(response).printBody()
		
        Assert.assertThat(body, Matchers.not(Matchers.containsString("id_token")))
        Assert.assertThat(body, Matchers.containsString("::token::"))
    }

    @Test
    @Throws(Exception::class)
    fun tokenCannotBeIssued() {
        val client = ClientBuilder.aNewClient().build()
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
		
        context.checking(
            object : Expectations() {
                init {
                    oneOf(tokens).issueToken(
                        with(
                            any(
                                TokenRequest::class.java,
                            ),
                        ),
                    )
                    will(returnValue(TokenResponse(false, null, "")))
                }
            },
        )
		
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
        val client = ClientBuilder.aNewClient().build()
        val anyTime = DateTime()
        val identity = IdentityBuilder.aNewIdentity().withId("::user_id::").build()
        val authorization = AuthorizationBuilder.newAuthorization().build()
		
        context.checking(
            object : Expectations() {
                init {
                    oneOf(tokens).issueToken(
                        TokenRequest(
                            grantType = GrantType.AUTHORIZATION_CODE,
                            client = client,
                            identity = identity,
                            scopes = authorization.scopes,
                            `when` = anyTime,
                            params = mapOf("::index::" to "::1::"),
                        ),
                    )
                    will(returnValue(TokenResponse(false, null, "")))
                }
            },
        )
		
        controller.execute(
            client,
            identity,
            authorization.scopes,
            ParamRequest(emptyMap()),
            anyTime,
            ImmutableMap.of("::index::", "::1::"),
        )
    }
}
