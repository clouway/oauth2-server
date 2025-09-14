package com.clouway.oauth2.exampleapp.storage

import com.clouway.friendlyserve.Request
import com.clouway.oauth2.ResourceOwnerIdentityFinder
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.exampleapp.UserRepository
import com.clouway.oauth2.token.FindIdentityRequest
import com.clouway.oauth2.token.FindIdentityResult
import com.clouway.oauth2.token.GrantType
import com.clouway.oauth2.token.Identity
import com.clouway.oauth2.token.IdentityFinder
import com.clouway.oauth2.token.ServiceAccount
import com.clouway.oauth2.token.User
import com.google.common.base.Optional

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
internal class InMemoryUserRepository :
    IdentityFinder,
    ResourceOwnerIdentityFinder,
    UserRepository {
    override fun find(
        request: Request,
        instantTime: DateTime,
    ): Optional<String> {
        // get session id from cookie
        // and retrieve user information for that SID
        for (sid in request.cookie("SID")) {
            return Optional.of("testUserID")
        }
		
        return Optional.absent()
    }

    override fun findIdentity(request: FindIdentityRequest): FindIdentityResult =
        if (request.subject is com.clouway.oauth2.token.Subject.User) {
            FindIdentityResult.User(
                Identity(
                    id = "testUserID",
                    name = "testUser",
                    givenName = "test User",
                    familyName = "User Family",
                    email = "test@clouway.com",
                    picture = null,
                    claims = emptyMap(),
                ),
            )
        } else {
            FindIdentityResult.ServiceAccountClient(
                ServiceAccount(
                    clientId = "customerapp@testapp.com",
                    clientEmail = "testapp@testapp.com",
                    name = "Test App",
                    customerId = null,
                    claims = emptyMap(),
                ),
            )
        }

    override fun load(userId: String): Optional<User> = Optional.of(User("testUserID", "test@clouway.com", "Ivan Stefanov"))
}
