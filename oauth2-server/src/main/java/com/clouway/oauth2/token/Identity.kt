package com.clouway.oauth2.token

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
data class Identity(
    val id: String,
    val name: String,
    val givenName: String,
    val familyName: String,
    val email: String,
    val picture: String?,
    val claims: Map<String, Any>,
)
