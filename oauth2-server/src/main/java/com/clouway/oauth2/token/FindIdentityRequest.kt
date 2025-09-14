package com.clouway.oauth2.token

import com.clouway.oauth2.common.DateTime

/**
 * @author Ianislav Nachev <qnislav.nachev></qnislav.nachev>@clouway.com>
 */
data class FindIdentityRequest(
    val subjectKind: SubjectKind,
    val subject: String,
    val grantType: GrantType,
    val instantTime: DateTime,
    val params: Map<String, String>,
    val clientId: String? = null,
)
