package com.clouway.oauth2.client

import com.google.common.base.Optional

/**
 * ClientAuthorizer is an authorized used to authorize passed
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
interface ClientFinder {
	fun findClient(clientId: String): Optional<Client>
}
