package com.clouway.oauth2.client;

import com.clouway.oauth2.jwt.Jwt;
import com.google.common.base.Optional;

/**
 * ServiceAccountRepository is a repository which is responsible for retrieving of ServiceAccount's.
 * <p/>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ServiceAccountRepository {
  /**
   * Finds ServiceAccount for the provided claimSet.
   *
   * @param claimSet the claim set of which service account is requested
   * @return the ServiceAccount associated with the provided claim or absent value
   */
  Optional<ServiceAccount> findServiceAccount(Jwt.ClaimSet claimSet);

}
