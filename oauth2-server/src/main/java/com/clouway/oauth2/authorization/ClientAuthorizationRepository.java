package com.clouway.oauth2.authorization;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.client.Client;
import com.google.common.base.Optional;

/**
 * ClientAuthorizationRepository is a repository which is keeping records for the authroziations
 * that are performed for the client applications.
 *
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthorizationRepository {

  /**
   * Finds authorization that is associated with the provided authCode.
   *
   * @param client   the client for which authorization was issued
   * @param authCode the code of the authorization
   * @param instant  the time on which check is performed
   * @return the authorization or absent value
   */
  Optional<Authorization> findAuthorization(Client client, String authCode, DateTime instant);

  /**
   * Authorize authorizes the identity
   *
   * @return the authorization or absent value if identity was not authorized
   */
  Optional<Authorization> authorize(AuthorizationRequest authorizationRequest);

}