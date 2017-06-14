package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.google.common.base.Optional;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class JjwtIdTokenFactory implements IdTokenFactory {
  private final ClientKeyStore clientKeyStore;

  public JjwtIdTokenFactory(ClientKeyStore clientKeyStore) {
    this.clientKeyStore = clientKeyStore;
  }

  @Override
  public Optional<String> create(String host, String clientId, Identity identity, Long ttl, DateTime instant) {
    Map<String, Block> certificates = clientKeyStore.privateCertificates();

    if (certificates == null || certificates.isEmpty()) {
      return Optional.absent();
    }

    String certKey = randomKey(certificates);
    Pem.Block key = certificates.get(certKey);

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
            .setHeaderParam("cid", certKey)//CertificateId - the ID of the certificate that the token was signed with.
            .setClaims(claims)
            .setIssuedAt(new Date(instant.timestamp()))
            .setExpiration(new Date(instant.timestamp() + ttl))
            .signWith(SignatureAlgorithm.RS256, parsePem(key))
            .compact());
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
}
