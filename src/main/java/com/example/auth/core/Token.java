package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Token {
  public final String value;
  public final String type;

  public Token(String value, String type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Token token = (Token) o;

    if (type != null ? !type.equals(token.type) : token.type != null) return false;
    if (value != null ? !value.equals(token.value) : token.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }
}