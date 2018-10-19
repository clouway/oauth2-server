package com.clouway.oauth2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public abstract class OAuth2Servlet extends HttpServlet {
  private OAuth2ApiSupport apiSupport;

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    apiSupport = new OAuth2ApiSupportFactory().create(config());
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.service(req, resp);

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    apiSupport.serve(req, resp);
  }

  protected abstract OAuth2Config config();

}
