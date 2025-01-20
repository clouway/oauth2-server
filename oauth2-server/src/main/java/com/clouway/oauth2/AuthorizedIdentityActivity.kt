package com.clouway.oauth2

import com.clouway.friendlyserve.Request
import com.clouway.friendlyserve.Response
import com.clouway.oauth2.client.Client
import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.token.Identity

/**
 * AuthorizedIdentityActivity is an activity which is represents the authorization client.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface AuthorizedIdentityActivity {
    /**
     * Handles client request of authorized client and returns response.
     *
     * @param client  the client of which request will be handled
     * @param request the request
     * @param instant the time of which client was requested access
     * @param params
     * @return the response
     */
    fun execute(
        client: Client,
        identity: Identity,
        scopes: Set<String>,
        request: Request,
        instant: DateTime,
        params: Map<String, String>,
    ): Response
}
