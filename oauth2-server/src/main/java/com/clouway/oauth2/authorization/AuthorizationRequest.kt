package com.clouway.oauth2.authorization

import com.clouway.oauth2.codechallenge.CodeChallenge
import java.time.LocalDateTime

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
data class AuthorizationRequest(
	@JvmField val clientId: String,
	@JvmField val identityId: String,
	@JvmField val responseType: String,
	@JvmField val scopes: Set<String>,
	@JvmField val codeChallenge: CodeChallenge,
	@JvmField val params: Map<String, String>,
	@JvmField val time: LocalDateTime
)