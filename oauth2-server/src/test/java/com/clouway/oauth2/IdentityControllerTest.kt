package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.RsText
import com.clouway.friendlyserve.testing.ParamRequest
import com.clouway.friendlyserve.testing.RsPrint
import com.clouway.oauth2.common.DateTime
import com.google.common.base.Optional
import com.google.common.collect.ImmutableMap
import org.hamcrest.Matchers
import org.jmock.Expectations
import org.jmock.auto.Mock
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URLDecoder
import java.net.URLEncoder
import javax.annotation.Resource
import kotlin.Throws

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IdentityControllerTest {
	@Rule
	@JvmField
	var context = JUnitRuleMockery()
	
	private val identityFinder = context.mock(ResourceOwnerIdentityFinder::class.java)
	private val identityActivity = context.mock(IdentityActivity::class.java)
	
	@Test
	@Throws(IOException::class)
	fun happyPath() {
		val identityController = IdentityController(identityFinder, identityActivity, "")
		val request: Request = ParamRequest(
			ImmutableMap.of(
				"client_id", "::client_id::"
			)
		)
		val anyInstantTime = DateTime()
		context.checking(object : Expectations() {
			init {
				oneOf(identityFinder).find(request, anyInstantTime)
				will(returnValue(Optional.of("::identity_id::")))
				oneOf(identityActivity).execute("::identity_id::", request, anyInstantTime)
				will(returnValue(RsText("test response")))
			}
		})
		val response = identityController.handleAsOf(request, anyInstantTime)
		Assert.assertThat(RsPrint(response).printBody(), Matchers.`is`(Matchers.equalTo("test response")))
	}
	
	@Test
	@Throws(IOException::class)
	fun userWasNotAuthorized() {
		val identityController = IdentityController(identityFinder, identityActivity, "/r/oauth/login?continue=")
		val request: Request = ParamRequest(ImmutableMap.of("client_id", "::client1::"))
		val anyInstantTime = DateTime()
		context.checking(object : Expectations() {
			init {
				oneOf(identityFinder).find(request, anyInstantTime)
				will(returnValue(Optional.absent<Any>()))
			}
		})
		val response = identityController.handleAsOf(request, anyInstantTime)
		val status = response.status()
		Assert.assertThat(status.code, Matchers.`is`(Matchers.equalTo(HttpURLConnection.HTTP_MOVED_TEMP)))
		Assert.assertThat(
			status.redirectUrl,
			Matchers.`is`(Matchers.equalTo("/r/oauth/login?continue=%2F%3Fclient_id%3D%3A%3Aclient1%3A%3A"))
		)
	}
	
	@Test
	@Throws(IOException::class)
	fun promptConsentWasRequested() {
		val identityController = IdentityController(identityFinder, identityActivity, "/r/oauth/login?continue=")
		val request: Request = ParamRequest(mapOf(
			"response_type" to "code",
			"redirect_uri" to "https://portal/o/oauth2/callback",
			"state" to "mystate1",
			"prompt" to "consent"
		))
		val anyInstantTime = DateTime()
		context.checking(object : Expectations() {
			init {
				never(identityFinder)
			}
		})
		val response = identityController.handleAsOf(request, anyInstantTime)
		val status = response.status()
		Assert.assertThat(status.code, Matchers.`is`(Matchers.equalTo(HttpURLConnection.HTTP_MOVED_TEMP)))
		Assert.assertThat(
			URLDecoder.decode(status.redirectUrl, "UTF-8"),
			Matchers.`is`(Matchers.equalTo("/r/oauth/login?continue=/?response_type=code&redirect_uri=https://portal/o/oauth2/callback&state=mystate1"))
		)
	}
}