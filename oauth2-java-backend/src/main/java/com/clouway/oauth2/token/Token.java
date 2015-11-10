package com.clouway.oauth2.token;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Token {
  public final String value;
  public final String type;
  public String refreshToken;
  public final String userId;
  public final Long expiresInSeconds;
  public final Date creationDate;


  public Token(String value, String type, String refreshToken, String userId, Long expiresInSeconds, Date creationDate) {
    this.value = value;
    this.type = type;
    this.refreshToken = refreshToken;
    this.userId = userId;
    this.expiresInSeconds = expiresInSeconds;
    this.creationDate = creationDate;
  }


  public boolean isExpiredOn(Date date){
    Date expirationDate = new Date(creationDate.getTime() + expiresInSeconds * 1000);
    return date.after(expirationDate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Token)) return false;

    Token token = (Token) o;

    if (creationDate != null ? !creationDate.equals(token.creationDate) : token.creationDate != null) return false;
    if (expiresInSeconds != null ? !expiresInSeconds.equals(token.expiresInSeconds) : token.expiresInSeconds != null)
      return false;
    if (type != null ? !type.equals(token.type) : token.type != null) return false;
    if (value != null ? !value.equals(token.value) : token.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (expiresInSeconds != null ? expiresInSeconds.hashCode() : 0);
    result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
    return result;
  }
}