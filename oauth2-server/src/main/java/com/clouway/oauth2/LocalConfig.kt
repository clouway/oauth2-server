package com.clouway.oauth2

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
object LocalConfig {
    fun jwtIssuerName(): String? = System.getenv("IDP_CONFIG_JWT_ISSUER")
}
