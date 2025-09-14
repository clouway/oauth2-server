package com.clouway.oauth2;

import com.clouway.friendlyserve.FkRegex;
import com.clouway.friendlyserve.RequestHandlerMatchingParam;
import com.clouway.friendlyserve.RequiresParam;
import com.clouway.friendlyserve.TkFork;
import com.clouway.friendlyserve.servlets.ServletApiSupport;
import com.clouway.oauth2.codechallenge.AuthorizationCodeVerifier;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.clouway.oauth2.jws.Signature;
import com.clouway.oauth2.jws.SignatureFactory;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.clouway.oauth2.token.JjwtIdTokenFactory;
import com.google.common.base.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mihail Lesikov (mihail.lesikov@clouway.com)
 */
public class OAuth2ApiSupportFactory {

  public OAuth2ApiSupport create(OAuth2Config config) {
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


    JjwtIdTokenFactory idTokenFactory = new JjwtIdTokenFactory(config.keyStore());
    TkFork fork = new TkFork(
            new FkRegex(".*/auth",
                    new InstantaneousRequestController(
                            new IdentityController(
                                    config.resourceOwnerIdentityFinder(),
                                    new ClientAuthorizationActivity(config.clientAuthorizer()), config.loginPageUrl())
                    )
            ),
            new FkRegex(".*/token",
                    new TkFork(
                            new RequestHandlerMatchingParam("grant_type", "authorization_code",
                                    new InstantaneousRequestController(
                                            new ClientAuthenticationCredentialsRequest(
                                                    new AuthCodeAuthorization(
                                                            config.clientAuthorizer(),
                                                            new CodeExchangeVerificationFlow(
                                                                    new AuthorizationCodeVerifier(),
                                                                    new IdentityAuthorizationActivity(
                                                                            config.identityFinder(),
                                                                            new IssueNewTokenActivity(
                                                                                    config.tokens(),
                                                                                    idTokenFactory)
                                                                    )
                                                            )
                                                    )
                                            ))
                            ),
                            new RequestHandlerMatchingParam("grant_type", "refresh_token",
                                    new InstantaneousRequestController(
                                            new ClientAuthenticationCredentialsRequest(
                                                    new ClientController(
                                                            config.clientFinder(),
                                                            new RefreshTokenActivity(config.tokens(), idTokenFactory, config.identityFinder())
                                                    ))

                                    )
                            ),
                            // JWT Support
                            new RequestHandlerMatchingParam("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer", new RequiresParam("assertion",
                                    new InstantaneousRequestController(
                                            new JwtController(
                                                    signatureFactory,
                                                    config.tokens(),
                                                    config.jwtKeyStore(),
                                                    config.identityFinder(),
                                                    idTokenFactory
                                            )))
                            ),

                            // Token Exchange Support
                            new RequestHandlerMatchingParam("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange",
                                new RequiresParam("subject_token",
                                    new InstantaneousRequestController(
                                        new ClientAuthenticationCredentialsRequest(
                                            new TokenExchangeController(
                                                config.tokens(),
                                                config.identityFinder(),
                                                idTokenFactory
                                            )
                                        )
                                    )
                                )
                            )
                    )
            ),
            new FkRegex(".*/revoke",
                    new RequiresParam("token",
                            new InstantaneousRequestController(
                                    new ClientAuthenticationCredentialsRequest(
                                            new RevokeTokenController(config.clientFinder(), config.tokens())
                                    )
                            )
                    )
            ),
            new FkRegex(".*/tokenInfo",
                    new RequiresParam("access_token",
                            new InstantaneousRequestController(
                                    new TokenInfoController(config.tokens(), config.identityFinder(), idTokenFactory)
                            )
                    )
            ),
            new FkRegex(".*/userInfo",
                    new RequiresParam("access_token",
                            new InstantaneousRequestController(
                                    new UserInfoController(config.identityFinder(), config.tokens())
                            ))
            ),
            new FkRegex(".*/certs", new PublicCertsController(config.keyStore()))
    );


    final ServletApiSupport servletApiSupport = new ServletApiSupport(fork);


    return new OAuth2ApiSupport() {
      @Override
      public void serve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        servletApiSupport.serve(req, resp);
      }
    };
  }
}
