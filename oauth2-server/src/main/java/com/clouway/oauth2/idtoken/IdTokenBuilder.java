package com.clouway.oauth2.idtoken;


import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.clouway.oauth2.jwt.Jwt.ClaimSet;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.google.common.io.BaseEncoding;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class IdTokenBuilder {

  private Header header;
  private ClaimSet claims;
  private Pem.Block key;

  public static IdTokenBuilder newIdToken() {
    return new IdTokenBuilder();
  }

  public IdTokenBuilder header(Header header) {
    this.header = header;
    return this;
  }

  public IdTokenBuilder claims(ClaimSet claims) {
    this.claims = claims;
    return this;
  }

  public IdTokenBuilder signWith(Pem.Block key) {
    this.key = key;
    return this;
  }

  public String build() {
    String headerString = BaseEncoding.base64Url().omitPadding().encode(header.toString().getBytes());
    String claimsString = BaseEncoding.base64Url().omitPadding().encode(claims.toString().getBytes());

    String unsignedToken = String.format("%s.%s", headerString, claimsString);
    RsaJwsSignature signature = new RsaJwsSignature(unsignedToken.getBytes());

    return String.format("%s.%s", unsignedToken, signature.sign(unsignedToken.getBytes(), key));
  }
}
