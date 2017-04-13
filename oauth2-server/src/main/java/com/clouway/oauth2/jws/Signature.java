package com.clouway.oauth2.jws;

import com.clouway.oauth2.jws.Pem.Block;

import java.security.Key;
import java.security.KeyPair;

/**
 * Signature is representing a single JWS signature that is applied over received messages.
 * <p/>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface Signature {

  /**
   * Verify is verifying Signature using the provided privateKey as PEM file.
   * <p/>
   *
   * @param content    the content to be verified
   * @param privateKey the private key used for verifying
   * @return true if signature is
   */
  boolean verifyWithPrivateKey(byte[] content, Pem.Block privateKey);

  /**
   * Verify is veryfying Signature using the provided publicKey as PEM file
   * <p/>
   *
   * @param content to be verified
   * @param key The public key to verify the content with
   * @return
   */
  boolean verify(byte[] content, Block key);

}
