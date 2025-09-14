package com.clouway.oauth2.token

/**
 * Represents the subject of an OAuth2 token, which can be either a user or a service account.
 *
 * @since 1.0.0
 * @author Miroslav Genov (
 */
sealed interface Subject {
    fun kind(): String

    fun id(): String

    data class User(
        val id: String,
    ) : Subject {
        override fun kind(): String = "user"

        override fun id(): String = id
    }

    data class ServiceAccount(
        val clientEmail: String,
    ) : Subject {
        override fun kind(): String = "service_account"

        override fun id(): String = clientEmail
    }
}
