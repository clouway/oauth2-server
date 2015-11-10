package com.clouway.oauth2.app;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthBootstrap {
  private static final Integer PORT = 9002;

  public static void main(String[] args) {

    AppServer server = new AppServer(PORT);
    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}