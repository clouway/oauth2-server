package com.example.auth.core;

import com.example.auth.core.session.Session;
import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ResourceOwnerAuthentication {
  Optional<Session> auth(String username, String password, String remoteAddress);
}