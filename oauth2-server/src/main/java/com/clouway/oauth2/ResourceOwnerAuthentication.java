package com.clouway.oauth2;

import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ResourceOwnerAuthentication {
  Optional<Session> auth(String username, String password, String remoteAddress);
}