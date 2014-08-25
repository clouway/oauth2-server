package com.example.auth.core.user;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class User {
  public final String id;
  public final String email;
  public final String name;

  public User(String id,String email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;

    User user = (User) o;

    if (email != null ? !email.equals(user.email) : user.email != null) return false;
    if (id != null ? !id.equals(user.id) : user.id != null) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
