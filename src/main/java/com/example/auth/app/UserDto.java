package com.example.auth.app;

import com.google.gson.annotations.SerializedName;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class UserDto {

  @SerializedName("id")
  public final String id;
  @SerializedName("email")
  public final String email;
  @SerializedName("fullName")
  public final String name;

  public UserDto(String id, String email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserDto)) return false;

    UserDto userDto = (UserDto) o;

    if (email != null ? !email.equals(userDto.email) : userDto.email != null) return false;
    if (id != null ? !id.equals(userDto.id) : userDto.id != null) return false;
    if (name != null ? !name.equals(userDto.name) : userDto.name != null) return false;

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
