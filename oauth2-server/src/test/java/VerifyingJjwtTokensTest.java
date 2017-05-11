import com.clouway.oauth2.Identity;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.jws.ReadPemFilesTest;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class VerifyingJjwtTokensTest {
  private Pem pem = new Pem();

  @Test
  public void tokenIsValidTest() throws Exception {
    Pem.Block block = pem.parse(ReadPemFilesTest.class.getResourceAsStream("secret.pem"));

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
    RsaJwsSignature rsaJwsSignature = new RsaJwsSignature(BaseEncoding.base64Url().decode(parts[2]));

    boolean tokenIsValid = rsaJwsSignature.verifyWithPrivateKey(tokenWithoutSignature.getBytes(), block);
    assertTrue(tokenIsValid);
  }

  @Test
  public void tokenIsNotValidTest() throws Exception {
    Pem pem = new Pem();
    Pem.Block block = pem.parse(ReadPemFilesTest.class.getResourceAsStream("secret.pem"));
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
    RsaJwsSignature rsaJwsSignature = new RsaJwsSignature(BaseEncoding.base64Url().decode(tokenHasBeenTemperedWithSignature));

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
