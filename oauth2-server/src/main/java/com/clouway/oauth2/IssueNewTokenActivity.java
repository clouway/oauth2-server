package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.clouway.oauth2.idtoken.IdTokenBuilder.newIdToken;
import static com.clouway.oauth2.jwt.Jwt.ClaimSet.newClaimSet;

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity implements ClientActivity {
  private final Tokens tokens;
  private final IdentityFinder identityFinder;
  private final ClientAuthorizationRepository clientAuthorizationRepository;
  private final ClientKeyStore keyStore;

  IssueNewTokenActivity(Tokens tokens, IdentityFinder identityFinder, ClientAuthorizationRepository clientAuthorizationRepository, ClientKeyStore keyStore) {
    this.tokens = tokens;
    this.identityFinder = identityFinder;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
    this.keyStore = keyStore;
  }

  @Override
  public Response execute(Client client, Request request, DateTime instant) {
    String authCode = request.param("code");

    Optional<Authorization> possibleResponse = clientAuthorizationRepository.findAuthorization(client, authCode);

    if (!possibleResponse.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Authorization authorization = possibleResponse.get();

    Optional<Identity> possibleIdentity = identityFinder.findIdentity(authorization.identityId, GrantType.AUTHORIZATION_CODE, instant);
    if (!possibleIdentity.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Identity identity = possibleIdentity.get();
    Map<String, Block> certificates = keyStore.publicCertificates();
    String certKey = randomKey(certificates);
    Pem.Block key = certificates.get(certKey);


    TokenResponse response = tokens.issueToken(GrantType.AUTHORIZATION_CODE, client, identity, authorization.scopes, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidRequest("Token cannot be issued.");
    }

    BearerToken accessToken = response.accessToken;
    String host = request.header("Host");

    String idToken = newIdToken().header(new Header("RS256"))
            .claims(newClaimSet()
                    .iss(host)
                    .aud(client.id)
                    .sub(identity.id())
                    .iat(instant.timestamp())
                    .exp(accessToken.expirationTimestamp())
                    .certId(certKey)
                    .customClaims(identity.claims())
                    .build())
            .signWith(key)
            .build();

    return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), response.refreshToken, idToken);
  }

  private String randomKey(Map<String, Block> map) {
    Random random = new Random();
    List<String> certId = new ArrayList<>(map.keySet());
    return certId.get(random.nextInt(certId.size()));
  }
}
