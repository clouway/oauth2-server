package com.clouway.oauth2.user;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserIdFinder {

  String find(String sessionId);

}
