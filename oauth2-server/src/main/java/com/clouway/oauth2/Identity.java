package com.clouway.oauth2;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class Identity {
  private Long id;
  private String name;
  private String givenName;
  private final String familyName;
  private String email;
  private String picture;

  public Identity(Long id, String name, String givenName, String familyName, String email, String picture) {
    this.id = id;
    this.name = name;
    this.givenName = givenName;
    this.familyName = familyName;
    this.email = email;
    this.picture = picture;
  }

  public String name() {
    return name;
  }

  public String email() {
    return email;
  }

  public String givenName() {
    return givenName;
  }

  public String familyName() {
    return familyName;
  }

  public Long id() {
    return id;
  }
}
