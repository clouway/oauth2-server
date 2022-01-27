package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.common.DateTime;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface InstantaneousRequest {

  Response handleAsOf(Request request, DateTime instantTime);

}
