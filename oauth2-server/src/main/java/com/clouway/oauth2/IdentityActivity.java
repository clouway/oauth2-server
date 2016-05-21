package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;

/**
 * IdentityActivity is an activity which is executed for authorised users.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface IdentityActivity {

  Response execute(String identityId, Request request);

}
