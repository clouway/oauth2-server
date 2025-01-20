package com.clouway.oauth2.token

import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime

/**
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
data class TokenRequest(
    val grantType: GrantType,
    val client: Client,
    val identity: Identity? = null,
    val serviceAccount: ServiceAccount? = null,
    val scopes: Set<String>,
    val `when`: DateTime,
    val params: Map<String, String>,
)
