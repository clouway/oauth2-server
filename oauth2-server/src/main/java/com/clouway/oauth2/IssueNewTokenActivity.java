package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity implements AuthorizedClientActivity {
  private final Tokens tokens;
  private final ClientKeyStore keyStore;

  IssueNewTokenActivity(Tokens tokens, ClientKeyStore keyStore) {
    this.tokens = tokens;
    this.keyStore = keyStore;
  }

  @Override
  public Response execute(Client client, Identity identity, Set<String> scopes, Request request, DateTime instant) {
    Map<String, Block> certificates = keyStore.privateCertificates();
    String certKey = randomKey(certificates);
    Pem.Block key = certificates.get(certKey);

    TokenResponse response = tokens.issueToken(GrantType.AUTHORIZATION_CODE, client, identity, scopes, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidRequest("Token cannot be issued.");
    }

    BearerToken accessToken = response.accessToken;
    String host = request.header("Host");

    String idToken = Jwts.builder()
            .setHeaderParam("cid", certKey)//CertificateId - the ID of the certificate that the token was signed with.
            .setIssuer(host)
            .setAudience(client.id)
            .setSubject(identity.id())
            .setIssuedAt(instant.asDate())
            .setExpiration(new Date(accessToken.expirationTimestamp()))
            .claim("identity", identityAsJson(identity).toString())
            .signWith(SignatureAlgorithm.RS256, parsePem(key))
            .compact();

    return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), response.refreshToken, idToken);
  }

  private String randomKey(Map<String, Block> map) {
    Random random = new Random();
    List<String> certId = new ArrayList<>(map.keySet());
    return certId.get(random.nextInt(certId.size()));
  }

  private PrivateKey parsePem(Pem.Block privateKey) {
    try {
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getBytes());
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }

  private JsonObject identityAsJson(Identity identity) {
    JsonObject o = new JsonObject();

    o.addProperty("id", identity.id());
    o.addProperty("name", identity.name());
    o.addProperty("email", identity.email());
    o.addProperty("given_name", identity.givenName());
    o.addProperty("family_name", identity.familyName());

    Map<String, Object> claims = identity.claims();

    for (String key : claims.keySet()) {
      Object value = claims.get(key);

      if (value instanceof String) {
        o.addProperty(key, (String) value);
      }
      if (value instanceof Number) {
        o.addProperty(key, (Number) value);
      }

      if (value instanceof Boolean) {
        o.addProperty(key, (Boolean) value);
      }
    }
    return o;
  }
}
