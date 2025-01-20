package com.clouway.oauth2.token

/**
 * IdentityFinder is finding the Identity of the request.
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
interface IdentityFinder {
    /**
     * Finds identity of the resource provider by providing it's id and the GrantType that was requested.
     *
     * @param request  find identity request
     * @return the associated identity by that id or absent value if it's not available.
     */
    fun findIdentity(request: FindIdentityRequest): FindIdentityResult
}

/**
 * FindIdentityResult is a result of the findIdentity operation. It can be either User, Client or NotFound depending on
 * the request.
 */
sealed interface FindIdentityResult {
    data class User(
        val identity: Identity,
    ) : FindIdentityResult

    data class ServiceAccountClient(
        val serviceAccount: ServiceAccount,
    ) : FindIdentityResult

    object NotFound : FindIdentityResult

    data class Error(
        val message: String,
    ) : FindIdentityResult
}
