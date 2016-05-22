package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.Session;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface SessionSecurity {
  Boolean exists(Session session);
}