package com.clouway.oauth2.jws;

import com.clouway.oauth2.jws.Pem.Block;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import static com.google.common.io.BaseEncoding.base64;

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

  @Override
  public boolean verify(byte[] content, Block key) {
    try {
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key.getBytes());
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PublicKey publicKey = keyFactory.generatePublic(keySpec);
      Signature sig = Signature.getInstance("SHA256withRSA");

      sig.initVerify(publicKey);
      sig.update(content);

      return sig.verify(signature);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public String sign(byte[] content, Pem.Block privateKey) {
    try {
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getBytes());
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PrivateKey privKey = kf.generatePrivate(keySpec);

      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initSign(privKey);

      sig.update(content);

      byte[] signed = sig.sign();
      return base64().omitPadding().encode(signed);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


}
