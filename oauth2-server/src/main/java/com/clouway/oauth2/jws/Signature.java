package com.clouway.oauth2.jws;

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
   * @param content the content to be verified
   * @param privateKeyPem the private key used for verifying
   * @return true if signature is
   */
  boolean verify(byte[] content, String privateKeyPem);

}
