package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 *
 *   A class that builds JWT tokens using JJWT builder
 */
public interface TokenBuilder {

  /**
   *
   * @param host that requested the token
   * @param clientId the client id
   * @param identity identity to which the token is issued to
   * @param ttl time to live for the token
   * @param instant the time at which the token was requested
   * @return an encoded id token
   */
  String issueToken(String host, String clientId, Identity identity, Long ttl, DateTime instant);
}
