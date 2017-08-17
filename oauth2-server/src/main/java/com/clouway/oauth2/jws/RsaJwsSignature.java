package com.clouway.oauth2.jws;

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

  public boolean verifyWithPrivateKey(byte[] content, Pem.Block privateKey) {
    try {

      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getBytes());
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
