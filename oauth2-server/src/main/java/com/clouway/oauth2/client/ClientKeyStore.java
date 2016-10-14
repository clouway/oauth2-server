package com.clouway.oauth2.client;

import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jwt.Jwt;
import com.clouway.oauth2.jwt.Jwt.ClaimSet;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.google.common.base.Optional;

/**
 * ClientKeyStore is a KeyStore which is responsible for retrieving of Key blocks for verifying
 * the JWT authorization.
 * <p/>
 * The implementations of this class should retrieve from secured store private keys which will
 * be used for verification of the received JWT tokens.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ClientKeyStore {

  /**
   * Finds associated KEY for the provided claim set.
   *
   * @param header the jwt header that specifies the type of the algorithm
   * @param claimSet the claim set of which service account is requested
   * @return the key for that service account
   */
  Optional<Pem.Block> findKey(Jwt.Header header, Jwt.ClaimSet claimSet);

}
