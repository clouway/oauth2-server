package com.clouway.oauth2.authorization

import com.clouway.oauth2.codechallenge.CodeChallenge

/**
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
data class Authorization(
    @JvmField val responseType: String = "authorization_code",
    @JvmField val clientId: String = "",
    @JvmField val subjectId: String = "",
    @JvmField val code: String,
    @JvmField val scopes: Set<String> = setOf(),
    @JvmField val redirectUrls: Set<String> = setOf(),
    @JvmField val codeChallenge: CodeChallenge,
    @JvmField val params: Map<String, String>,
)
