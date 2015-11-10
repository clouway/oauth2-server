package com.example.auth.core.session;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface SessionSecurity {
  Boolean exists(Session session);
}