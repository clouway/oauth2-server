package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.google.common.base.Optional;

/**
 * IdentityActivity is an activity which is executed for authorised users.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface IdentityActivity {

  Response execute(Client client, String userId, Request request);

}
