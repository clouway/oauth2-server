package com.clouway.oauth2.token

/**
 * Represents the subject of an OAuth2 token, which can be either a user or a service account.
 *
 * @since 1.0.0
 * @author Miroslav Genov (
 */
sealed interface Subject {
    data class User(
        val id: String,
    ) : Subject

    data class ServiceAccount(
        val clientEmail: String,
    ) : Subject
}
