package com.clouway.oauth2.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface Request {

  String path();

  String param(String name);

  String header(String name);

  InputStream body() throws IOException;

}
