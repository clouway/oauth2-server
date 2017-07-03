package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;
import com.clouway.oauth2.KeyStore;
import com.clouway.oauth2.client.IdentityKeyPair;
import com.google.common.base.Optional;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class JjwtIdTokenFactory implements IdTokenFactory {
  private final KeyStore keyStore;

  public JjwtIdTokenFactory(KeyStore keyStore) {
    this.keyStore = keyStore;
  }

  @Override
  public Optional<String> create(String host, String clientId, Identity identity, Long ttl, DateTime instant) {
    List<IdentityKeyPair> keys = keyStore.getKeys();

    if (keys == null || keys.isEmpty()) {
      return Optional.absent();
    }

    IdentityKeyPair signingKey = randomKey(keys);

    Map<String, Object> claims = new LinkedHashMap<>();
    claims.put("iss", host);
    claims.put("aud", clientId);
    claims.put("sub", identity.id());
    claims.put("name", identity.name());
    claims.put("email", identity.email());
    claims.put("given_name", identity.givenName());
    claims.put("family_name", identity.familyName());
    claims.putAll(identity.claims());

    return Optional.of(Jwts.builder()
            .setHeaderParam("cid", signingKey.keyId)//CertificateId - the ID of the certificate that the token was signed with.
            .setClaims(claims)
            .setIssuedAt(new Date(instant.timestamp()))
            .setExpiration(new Date(instant.timestamp() + ttl))
            .signWith(SignatureAlgorithm.RS256, signingKey.privateKey)
            .compact());
  }

  private IdentityKeyPair randomKey(List<IdentityKeyPair> keys) {
    Random random = new Random();
    return keys.get(random.nextInt(keys.size()));
  }

}
