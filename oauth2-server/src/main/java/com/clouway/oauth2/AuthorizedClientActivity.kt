package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.authorization.Authorization
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
internal interface AuthorizedClientActivity {
	fun execute(authorization: Authorization, client: Client, request: Request, instant: DateTime?): Response
}