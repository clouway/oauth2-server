package com.clouway.oauth2.jws;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.RsaSigner;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static com.google.common.io.BaseEncoding.base64;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class RsaJwsSigner {
  private static final SignatureAlgorithm rsaAlgorithm = SignatureAlgorithm.forName("RS256");

  public static String sign(byte[] content, Pem.Block privateKey) {
    PrivateKey prKey = parsePem(privateKey);
    RsaSigner rsaSigner = new RsaSigner(rsaAlgorithm, prKey);
    byte[] signedContent = rsaSigner.sign(content);
    return tokenSignature(signedContent);
  }

  private static PrivateKey parsePem(Pem.Block privateKey) {
    try {
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getBytes());
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String tokenSignature(byte[] content) {
    return base64().omitPadding().encode(content);
  }
}
