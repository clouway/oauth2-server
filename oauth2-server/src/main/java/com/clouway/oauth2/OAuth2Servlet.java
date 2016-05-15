package com.clouway.oauth2;

import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.client.ServiceAccountRepository;
import com.clouway.oauth2.http.FkParams;
import com.clouway.oauth2.http.FkRegex;
import com.clouway.oauth2.http.HttpException;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.Status;
import com.clouway.oauth2.http.TkFork;
import com.clouway.oauth2.http.TkRequestWrap;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.clouway.oauth2.jws.Signature;
import com.clouway.oauth2.jws.SignatureFactory;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.clouway.oauth2.jwt.JwtController;
import com.clouway.oauth2.token.TokenRepository;
import com.clouway.oauth2.user.UserIdFinder;
import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public abstract class OAuth2Servlet extends HttpServlet {
  private TkFork fork;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    final SignatureFactory constSignatureFactory = new SignatureFactory() {
      @Override
      public Optional<Signature> createSignature(byte[] signatureValue, Header header) {
        return Optional.<Signature>of(new RsaJwsSignature(signatureValue));
      }
    };

    fork = new TkFork(
            new FkRegex(".*/authorize",
                    new IdentityController(clientRepository(), userIdFinder(), new AuthorizationActivity(clientAuthorizationRepository()))
            ),
            new FkRegex(".*/token",
                    new TkFork(
                            new FkParams("grant_type", "authorization_code", new ClientController(
                                    clientRepository(),
                                    new IssueNewTokenActivity(tokenRepository(), clientAuthorizationRepository()))
                            ),
                            new FkParams("grant_type", "refresh_token", new ClientController(
                                    clientRepository(),
                                    new RefreshTokenActivity(tokenRepository()))
                            ),
                            // JWT Support
                            new FkParams("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer", new JwtController(
                                    constSignatureFactory,
                                    tokenRepository(),
                                    serviceAccountRepository())
                            )
                    ))
    );
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    try {
      Response response = fork.ack(new TkRequestWrap(req));

      Status status = response.status();
      resp.setStatus(status.code);

      // Handle redirects
      if (status.code == HttpURLConnection.HTTP_MOVED_TEMP) {
        resp.sendRedirect(status.redirectUrl);

        return;
      }

      Map<String, String> header = response.header();
      for (String key : header.keySet()) {
        resp.setHeader(key, header.get(key));
      }

      ServletOutputStream out = resp.getOutputStream();

      try (InputStream inputStream = response.body()) {
        ByteStreams.copy(inputStream, out);
      }

      out.flush();

    } catch (HttpException e) {
      resp.setStatus(e.code());
    }
  }

  protected abstract ServiceAccountRepository serviceAccountRepository();

  protected abstract UserIdFinder userIdFinder();

  protected abstract ClientAuthorizationRepository clientAuthorizationRepository();

  protected abstract ClientRepository clientRepository();

  protected abstract TokenRepository tokenRepository();

}
