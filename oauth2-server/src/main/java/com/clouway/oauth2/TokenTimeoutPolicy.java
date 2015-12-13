package com.clouway.oauth2;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface TokenTimeoutPolicy {
  Duration refreshTokenTimeout();

  Duration authorizationTokenTimeout();
}
