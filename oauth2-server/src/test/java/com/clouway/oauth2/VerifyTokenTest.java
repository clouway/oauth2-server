package com.clouway.oauth2;

import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.google.common.io.BaseEncoding;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.clouway.oauth2.PemKeyGenerator.generatePrivateKey;
import static com.clouway.oauth2.PemKeyGenerator.generatePublicKey;
import static com.clouway.oauth2.idtoken.IdTokenBuilder.newIdToken;
import static com.clouway.oauth2.jwt.Jwt.ClaimSet.newClaimSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class VerifyTokenTest {
  private Map<String, Object> customClaims = new LinkedHashMap<String, Object>() {{
    put("name", "Foo");
    put("given_name", "John");
    put("family_name", "Doe");
    put("custom_claims", Collections.emptyList());
  }};

  private final Pem.Block privateKey = generatePrivateKey();
  private final String token = newIdToken().header(new Header("RS256"))
          .claims(newClaimSet()
                  .iss("provider.somedomain.com")
                  .sub("10769150350006150715113082367")
                  .iat(123456789L)
                  .customClaims(customClaims)
                  .build())
          .signWith(privateKey)
          .build();

  @Test
  public void tokenVerifiedWithPrivateKey() throws Exception {
    String parts[] = token.split("\\.");
    byte[] signatureValue = BaseEncoding.base64().decode(parts[2]);
    RsaJwsSignature signature = new RsaJwsSignature(signatureValue);
    String value = String.format("%s.%s", parts[0], parts[1]);

    boolean tokenVerified = signature.verifyWithPrivateKey(value.getBytes(), privateKey);
    assertThat(tokenVerified, is(true));
  }

  @Test
  public void tokenVerifiedWithPublicKey() throws Exception {
    byte[] signatureValue = BaseEncoding.base64().decode(token.split("\\.")[2]);
    RsaJwsSignature signature = new RsaJwsSignature(signatureValue);

    String tokenWithoutSignature = token.split("\\.")[0] + "." + token.split("\\.")[1];
    Block publicKey = generatePublicKey();

    boolean tokenVerified = signature.verify(tokenWithoutSignature.getBytes(), publicKey);
    assertThat(tokenVerified, is(true));
  }

  @Test
  public void invalidTokenTest() throws Exception {
    //Signature from another token
    byte[] signatureValue = BaseEncoding.base64().decode("TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
    RsaJwsSignature signature = new RsaJwsSignature(signatureValue);

    String tokenWithoutSignature = token.split("\\.")[0] + "." + token.split("\\.")[1];

    boolean tokenVerified = signature.verifyWithPrivateKey(tokenWithoutSignature.getBytes(), privateKey);
    assertThat(tokenVerified, is(false));
  }
}
