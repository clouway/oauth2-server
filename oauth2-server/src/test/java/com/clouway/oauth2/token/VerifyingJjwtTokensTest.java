package com.clouway.oauth2.token;

import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class VerifyingJjwtTokensTest {
  private Pem pem = new Pem();

  final String SECRET_KEY = "-----BEGIN PRIVATE KEY-----\n" +
          "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIga1VBV5S+diJ\n" +
          "2abh4YVHAWb7rlM9pnK1GVZOYE6xheJ1jsoxh03lUHjw8HtBR2USIAWG1a/kAAdz\n" +
          "fngrFFudpOO5RU1yEFnz2WNwy7gj3xu1erqtxmctOkyGA2hfATzHUfStSl8gyMXK\n" +
          "f3GURe4m+Y/qa1nI0ClGrJWbFdM9gz9YjRf3rAJsYZR17dGyBRIdlMeKF/QudP68\n" +
          "Dv/2/fssOhrIz5RZl2pkovWYidZxeRBl9XS9naoUZM8HvqoyUTE7ByjvDBR9qBkS\n" +
          "mCXJAUb82/LkQFLhufhnHCSC8b+kHWpBhiMqslVnR00mhiQHCW7PNtLfb6ugTZNi\n" +
          "cfBIaI1DAgMBAAECggEAEwXnTtrhqzSQPZ2sSPwxo5SJcnd3uDay85PlWCTJsqmS\n" +
          "xokwmjhd3aAaSpFoy88UQbNescyjp2VtpGWyf2Zl4hExfwcuZL/smTPpTLXHIpCb\n" +
          "/u1siH0GseHW+jINYHf+rVQ5gdDEcwAnuDGMdXpNVvceXC+7omWH6wZwDt26w26L\n" +
          "J31/aHwvjrlIbQq4PAS0gAh6CHUgyeOv8MkCR5rVhS9Db03U6QFRc79CMMghl906\n" +
          "C9rqRjDmIX3MEh/IiW/hJzmz8kPNL1RiUb5OI24ceDtWa2AzvGalwDS0SvtS8SBT\n" +
          "9jI0htKl8NGY6oknfkopw6LuVjZ2hOdgV3z26DJ2YQKBgQDbUlAnqdSLrtzepKZN\n" +
          "PJQqNgC2ATUVvpcE64ze48t8sV5lVsdceLllzVxD0hjh6ngqynDGJjzp+AEcS5LQ\n" +
          "nCYinVb0x8iIZlC5dIPDP85mIfYARFFHF20DD0HoMJfcxqeeJsaU24Pkh7Y8V/BC\n" +
          "as4DM8ByOreBk2a/VZaMWA9X8wKBgQCfVdqHBeZDOSoHPHZ9ApMsHAYJmp/0MBzv\n" +
          "vQda4lZehmJ9Hefr5J3tk4c4xH+FODAEqaWHoFe+Y95XuR69xkGiBiIHlhMK1xSA\n" +
          "1Gmn+WWPTjhPcNqGWXfeTnMZNWgZguo7e10fHNWQT2RklegJfB593E4K83Y+pG+b\n" +
          "4adiHMQZcQKBgCuI9lI5OvCTQFKNmllAiiSq3Y9DRBdR4sZeP3NLAmx5BMTW6fHo\n" +
          "IN0dW5A21yuZEEtmLeaXVoYW7ZmBQt5X8JX0Z3tlYN/6d1Go2DLcqorJePxqkzuq\n" +
          "YcA2uh1t7+cqI8GX7tlDjbXCXqExz4ZPjx9BmZTTJPP6n22hfqXTIRCTAoGAC498\n" +
          "Gn3YFhqIrRu68RkFupaR7ZJ1do8jGlXZucNgRt1zOea4lAnzV3BzyC+hnPXVrhDs\n" +
          "/KkqlJrEYBMDYvuGeY3+XBSMbyXpy+sde12B++LN/R2QDV1icBO7ECIq2mcAPa6W\n" +
          "tBIwgJbyDsY9nqqNv84DL5I4ixT9MA8wSNMTe1ECgYB329OIU8nIpn4KQDiFlNdg\n" +
          "VE+F2I7f4zNtM/NAeKoG7WGY6CO2sGmKKvT7g5Ql5/mbzsowkzaLNrzR0RQNEy5r\n" +
          "IsCHV4InIHYnIPxuxT2nWP33x1rlso7pB5xTf5q0ee+6ps25zWmBfEYlM72dPFCi\n" +
          "e16DdKBIzOaDt8ShlblC9g==\n" +
          "-----END PRIVATE KEY-----";

  @Test
  public void tokenIsValidTest() throws Exception {
    Pem.Block block = pem.parse(new ByteArrayInputStream(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));

    Identity identity = new Identity("123", "Pojo", "Foo", "Bar",
            "example@email.com", "", ImmutableMap.<String, Object>of("customerId", "::customer Id::",
            "customerName", "::customer name::", "isAdmin", false));

    Map<String, Object> claims = ImmutableMap.<String, Object>builder()
            .put("iss", "example.host")
            .put("aud", "123")
            .put("sub", identity.id())
            .put("iat", new Date())
            .put("exp", new Date())
            .put("name", identity.name())
            .put("email", identity.email())
            .put("given_name", identity.givenName())
            .put("family_name", identity.familyName())
            .putAll(identity.claims()).build();

    String idToken = Jwts.builder()
            .setHeaderParam("cid", "certKeyIdentifier")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, getPrivateKey(block))
            .compact();


    String parts[] = idToken.split("\\.");
    String tokenWithoutSignature = String.format("%s.%s", parts[0], parts[1]);
    com.clouway.oauth2.jws.RsaJwsSignature rsaJwsSignature = new com.clouway.oauth2.jws.RsaJwsSignature(BaseEncoding.base64Url().decode(parts[2]));

    boolean tokenIsValid = rsaJwsSignature.verifyWithPrivateKey(tokenWithoutSignature.getBytes(), block);
    assertTrue(tokenIsValid);
  }

  @Test
  public void tokenIsNotValidTest() throws Exception {
    Pem pem = new Pem();
    Pem.Block block = pem.parse(new ByteArrayInputStream(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
    Map<String, Object> claims = ImmutableMap.<String, Object>builder()
            .put("iss", "example.host")
            .put("aud", "123")
            .put("sub", "::subject::")
            .put("iat", new Date())
            .put("exp", new Date()).build();

    String idToken = Jwts.builder()
            .setHeaderParam("cid", "certKeyIdentifiers")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, getPrivateKey(block))
            .compact();

    String tokenHasBeenTemperedWithSignature = "mhkSKK6lcwNZ085PYfUc8uQbynCnMd25IcomnQ9Tkbxb9FvhA51Klgu_nmyQtvsqytYM0HfDCUA8jf2MprQ79xiu7DVMWQ9bS0W18PpUN6KNi5_L6KMxzNrS8DxRUliYYIcRYDh5xWfzXrDGpnw208vSfH1GD4PGY2z6JhlFkf0";

    String parts[] = idToken.split("\\.");
    String tokenWithoutSignature = String.format("%s.%s", parts[0], parts[1]);
    com.clouway.oauth2.jws.RsaJwsSignature rsaJwsSignature = new com.clouway.oauth2.jws.RsaJwsSignature(BaseEncoding.base64Url().decode(tokenHasBeenTemperedWithSignature));

    boolean tokenIsValid = rsaJwsSignature.verifyWithPrivateKey(tokenWithoutSignature.getBytes(), block);
    assertFalse(tokenIsValid);
  }

  private PrivateKey getPrivateKey(Block block) {
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(block.getBytes());
    KeyFactory kf;
    try {
      kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }
}
