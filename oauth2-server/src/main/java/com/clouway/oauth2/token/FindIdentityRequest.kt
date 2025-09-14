package com.clouway.oauth2.token

import com.clouway.oauth2.common.DateTime

/**
 * @author Ianislav Nachev <qnislav.nachev></qnislav.nachev>@clouway.com>
 */
data class FindIdentityRequest(
    val subject: Subject,
    val instantTime: DateTime,
    val clientId: String? = null,
    val params: Map<String, String> = emptyMap(),
)
