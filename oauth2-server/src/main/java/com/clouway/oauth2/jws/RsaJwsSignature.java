package com.clouway.oauth2.jws;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * RsaJwsSignature is an implementation of {@link com.clouway.oauth2.jws.Signature} that uses
 * RSA algorithm.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsaJwsSignature implements com.clouway.oauth2.jws.Signature {
  private byte[] signature;

  public RsaJwsSignature(byte[] signature) {
    this.signature = signature;
  }

  public boolean verify(byte[] content, String privateKeyPem) {
    try {
      Pem pem = new Pem();

      Pem.Block block = pem.parse(new ByteArrayInputStream(privateKeyPem.getBytes()));
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(block.getBytes());
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PrivateKey privKey = kf.generatePrivate(keySpec);

      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initSign(privKey);

      sig.update(content);

      byte[] checkSign = sig.sign();

      return Arrays.equals(signature, checkSign);

    } catch (Exception e) {
      return false;
    }
  }
}
