package com.example.auth;

import com.example.auth.jetty.OauthAuthorizationJettyServer;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthBootstrap {
  private static final Integer PORT = 9002;

  public static void main(String[] args) {

    OauthAuthorizationJettyServer server = new OauthAuthorizationJettyServer(PORT);
    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}