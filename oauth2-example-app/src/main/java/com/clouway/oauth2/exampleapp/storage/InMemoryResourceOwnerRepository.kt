package com.clouway.oauth2.exampleapp.storage

import com.clouway.oauth2.Session
import com.clouway.oauth2.exampleapp.ResourceOwner
import com.clouway.oauth2.exampleapp.ResourceOwnerAuthentication
import com.clouway.oauth2.exampleapp.ResourceOwnerStore
import com.clouway.oauth2.exampleapp.SessionSecurity
import com.clouway.oauth2.token.TokenGenerator
import com.google.common.base.Optional
import com.google.inject.Inject

/**
 * @author Ivan Stefanov <ivan.stefanov></ivan.stefanov>@clouway.com>
 */
internal class InMemoryResourceOwnerRepository
    @Inject
    constructor(
        private val tokenGenerator: TokenGenerator,
    ) : ResourceOwnerStore,
        ResourceOwnerAuthentication,
        SessionSecurity {
        private val resourceOwners = mutableMapOf("admin" to ResourceOwner("admin", "admin"))

        private val sessions: MutableSet<Session> = HashSet()

        override fun save(resourceOwner: ResourceOwner) {
            resourceOwners[resourceOwner.username] = resourceOwner
        }

        override fun auth(
            username: String,
            password: String,
            remoteAddress: String,
        ): Optional<Session> {
            if (resourceOwners.containsKey(username)) {
                if (password == resourceOwners[username]!!.password) {
                    val session = Session(tokenGenerator.generate())
				
                    sessions.add(session)
				
                    return Optional.of(session)
                }
            }
		
            return Optional.absent()
        }

        override fun exists(session: Session): Boolean = sessions.contains(session)
    }
