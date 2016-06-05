package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;

import java.util.Date;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface InstantaneousRequest {

  Response handleAsOf(Request request, Date instantTime);

}
