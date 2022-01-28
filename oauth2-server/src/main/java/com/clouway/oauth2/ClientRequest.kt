package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.ClientCredentials
import com.clouway.oauth2.common.DateTime

/**
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface ClientRequest {
	fun handleAsOf(request: Request, credentials: ClientCredentials, instant: DateTime): Response
}