package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.common.DateTime;

/**
 * IdentityActivity is an activity which is executed for authorised users.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface IdentityActivity {

  Response execute(String subjectId, Request request, DateTime instantTime);

}
