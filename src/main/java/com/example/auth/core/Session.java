package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Session {
  public final String value;

  public Session(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Session session = (Session) o;

    if (value != null ? !value.equals(session.value) : session.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Session{" +
            "value='" + value + '\'' +
            '}';
  }
}