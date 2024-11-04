package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.friendlyserve.RsRedirect
import com.clouway.oauth2.common.DateTime
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * IdentityController is responsible for determining the Identity of the caller. Sent request to this controller
 * should contain information about the client application and for Granting User (user session is determined from [Request]).
 *
 *
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
internal class IdentityController(
	private val identityFinder: ResourceOwnerIdentityFinder,
	private val identityActivity: IdentityActivity,
	private val loginPageUrl: String
) : InstantaneousRequest {
	override fun handleAsOf(request: Request, instantTime: DateTime): Response {
		val prompt = request.param("prompt")
		if (prompt == "consent") {
			val continueTo = queryParams(request)
			return RsRedirect(loginPageUrl + continueTo)
		}
		
		val optIdentity = identityFinder.find(request, instantTime)
		// Browser should be redirected to login page when Identity is not found
		if (!optIdentity.isPresent) {
			val continueTo = queryParams(request)
			return RsRedirect(loginPageUrl + continueTo)
		}
		return identityActivity.execute(optIdentity.get(), request, instantTime)
	}
	
	private fun queryParams(request: Request): String {
		var params = ""
		for (param in request.names()) {
			if (param == "prompt") {
				continue
			}
			params += "&" + param + "=" + request.param(param)
		}
		return try {
			URLEncoder.encode(request.path() + "?" + params.substring(1, params.length), "UTF-8")
		} catch (e: UnsupportedEncodingException) {
			""
		}
	}
}