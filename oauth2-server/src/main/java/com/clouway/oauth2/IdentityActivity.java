package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;

/**
 * IdentityActivity is an activity which is executed for authorised users.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface IdentityActivity {

  Response execute(ResourceOwnerIdentity ownerIdentity, Request request);

}
