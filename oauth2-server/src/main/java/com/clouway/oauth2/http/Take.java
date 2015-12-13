package com.clouway.oauth2.http;

import java.io.IOException;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface Take {

  Response ack(Request request) throws IOException;

}
