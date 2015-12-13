package com.clouway.oauth2;

import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.FkParams;
import com.clouway.oauth2.http.FkRegex;
import com.clouway.oauth2.http.HttpException;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.TkFork;
import com.clouway.oauth2.http.TkRequestWrap;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.io.ByteStreams;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public abstract class OAuth2Servlet extends HttpServlet {

  private TkFork fork;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    fork = new TkFork(
            new FkRegex(".*/token",
                    new TkFork(
                            new FkParams("grant_type", "authorization_code", new ClientController(
                                    clientRepository(),
                                    new IssueNewTokenActivity(tokenRepository(), clientAuthorizationRepository()))
                            ),
                            new FkParams("grant_type", "refresh_token", new ClientController(
                                    clientRepository(),
                                    new RefreshTokenActivity(tokenRepository()))
                            )
                    ))
    );

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    PrintWriter writer = resp.getWriter();
    writer.print("GET operation is not supported.");
    writer.flush();
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    try {
      Response response = fork.ack(new TkRequestWrap(req));

      Map<String, String> header = response.header();
      for (String key : header.keySet()) {
        resp.setHeader(key, header.get(key));
      }

      ServletOutputStream out = resp.getOutputStream();
      ByteStreams.copy(response.body(), out);
      out.flush();

    } catch (HttpException e) {
      resp.setStatus(e.code());
    }
  }


  protected abstract ClientAuthorizationRepository clientAuthorizationRepository();

  protected abstract ClientRepository clientRepository();

  protected abstract TokenRepository tokenRepository();
}
