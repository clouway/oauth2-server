package com.clouway.oauth2.token;

import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;

/**
 * A class that builds JWT tokens using JJWT builder
 * <p>
 *   
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 *
 */
public interface IdTokenFactory {

  /**
   * Creates a new id token from the provided metadata.
   *
   * @param host     that requested the token
   * @param clientId the client id
   * @param identity identity to which the token is issued to
   * @param ttl      time to live for the token
   * @param instant  the time at which the token was requested
   * @return an encoded id token
   */
  Optional<String> create(String host, String clientId, Identity identity, Long ttl, DateTime instant);


}
