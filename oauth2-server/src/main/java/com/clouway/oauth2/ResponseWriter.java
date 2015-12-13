package com.clouway.oauth2;


/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface ResponseWriter {
  void badRequest();

  void writeResponse(Object response);
}
