package com.example.auth.core.user;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserIdFinder {

  String find(String sessionId);

}
