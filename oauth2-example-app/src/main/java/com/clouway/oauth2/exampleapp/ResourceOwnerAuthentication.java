package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.Session;
import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ResourceOwnerAuthentication {
  Optional<Session> auth(String username, String password, String remoteAddress);
}