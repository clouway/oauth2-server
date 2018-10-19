package com.clouway.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mihail Lesikov (mihail.lesikov@clouway.com)
 */
public interface OAuth2ApiSupport {

  void serve(HttpServletRequest req, HttpServletResponse resp) throws IOException;

}
