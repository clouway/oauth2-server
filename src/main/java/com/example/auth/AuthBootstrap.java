package com.example.auth;

import com.example.auth.jetty.OauthAuthorizationJettyServer;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthBootstrap {
  private static Integer PORT = 9002;
  private static OauthAuthorizationJettyServer server = new OauthAuthorizationJettyServer(PORT);

  public static void main(String[] args) {
    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}