package com.clouway.oauth2.jws;

import com.clouway.oauth2.jwt.Jwt.Header;
import com.google.common.base.Optional;

/**
 * SignatureFactory is a factory class which is creating a signature by providing it's signature value and a Header that
 * comes from the request to be able to determine the type of the Signature.
 * <p/>
 * Currently only RSA Signature is supported.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface SignatureFactory {

  /**
   * Finds signature that should be applied for the provided header.
   *
   * @param signatureValue as byte array
   * @param header the header to be used
   * @return the
   */
  Optional<Signature> createSignature(byte[] signatureValue, Header header);
}
