package com.clouway.oauth2.jwt;

import com.google.common.base.MoreObjects;

/**
 * Jwt stands for JSON Web Token and is the entry point of the JWT package.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class Jwt {

  /**
   * JSON object represented by the JOSE Header describe the cryptographic operations applied to the JWT
   * and optionally, additional properties of the JWT.
   *
   * @author Miroslav Genov (miroslav.genov@clouway.com)
   * @see <a href="https://tools.ietf.org/html/rfc7519#section-5">Jose Header</a>
   */
  public static class Header {

    public final String alg;

    @SuppressWarnings("unused")
    Header() {
      this(null);
    }

    public Header(String alg) {
      this.alg = alg;
    }
  }

  /**
   * ClaimSet contains information about JWT signature including the permissions being requested (scopes), the target of
   * the token, the issuer, the time token was issued and lifetime of the token.
   *
   * @author Miroslav Genov (miroslav.genov@clouway.com)
   * @see <a href="https://tools.ietf.org/html/rfc7519#section-4.1">Registered Claim Names</a>
   */
  public static final class ClaimSet {
    /**
     * Email address of the client_id of the application making access token request.
     */
    public final String iss;
    /**
     * Space delimited list of permissions of application requests
     */
    public final String scope;

    /**
     * Descriptor of the intended target of the assertion (Optional)
     */
    public final String aud;

    /**
     * The expiration time of assertion
     */
    public final Long exp;

    /**
     * The time assertion was issued
     */
    public final Long iat;

    /**
     * Token Type (Optional)
     */
    public final String typ;

    /**
     * Email for which the application is requested delegated access (Optional)
     */
    public final String sub;

    /**
     * Prn is an old name of sub. Keep setting it to be compatible with legacy OAuth2 clients
     */
    public final String prn;

    @SuppressWarnings("unused")
    ClaimSet() {
      this(null, null, null, null, null, null, null);
    }

    public ClaimSet(String iss, String scope, String aud, Long exp, Long iat, String typ, String sub) {
      this.iss = iss;
      this.scope = scope;
      this.aud = aud;
      this.exp = exp;
      this.iat = iat;
      this.typ = typ;
      this.sub = sub;
      this.prn = sub;

    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("iss", iss).add("scope", scope).add("aud", aud).add("exp", exp).add("iat", iat).add("typ", typ).toString();
    }
  }
}
