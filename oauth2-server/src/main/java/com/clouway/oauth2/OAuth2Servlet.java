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
import com.clouway.oauth2.user.IdentityFinder;
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

    final SignatureFactory signatureFactory = new SignatureFactory() {
      @Override
      public Optional<Signature> createSignature(byte[] signatureValue, Header header) {
        // It's vulnerable multiple algorithms to be supported, so server need to reject
        // any calls from not supported algorithms
        // More infromation could be taken from: https://auth0.com/blog/2015/03/31/critical-vulnerabilities-in-json-web-token-libraries/
        if (!"RS256".equals(header.alg)) {
          return Optional.absent();
        }
        return Optional.<Signature>of(new RsaJwsSignature(signatureValue));
      }
    };

    fork = new TkFork(
            new FkRegex(".*/authorize",
                    new IdentityController(identityFinder(), new ClientAuthorizationActivity(clientRepository(), clientAuthorizationRepository()))
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
                                    signatureFactory,
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

  protected abstract IdentityFinder identityFinder();

  protected abstract ClientAuthorizationRepository clientAuthorizationRepository();

  protected abstract ClientRepository clientRepository();

  protected abstract TokenRepository tokenRepository();

}
