import com.clouway.oauth2.Identity;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.jws.ReadPemFilesTest;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;

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
            "example@email.com", "", Collections.EMPTY_MAP);

    String idToken = Jwts.builder()
            .setHeaderParam("cid", "certKeyIdentifier")
            .setIssuer("host.app@example.com")
            .setAudience("aud")
            .setSubject(identity.id())
            .setIssuedAt(new Date())
            .setExpiration(new Date())
            .claim("identity", new JsonObject().toString())
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

    Identity identity = new Identity("123", "Pojos", "Foos", "Bars",
            "examples@email.com", "", Collections.EMPTY_MAP);

    String idToken = Jwts.builder()
            .setHeaderParam("cid", "certKeyIdentifiers")
            .setIssuer("anotherHost.app@example.com")
            .setAudience("aud")
            .setSubject(identity.id())
            .setIssuedAt(new Date())
            .setExpiration(new Date())
            .claim("identity", new JsonObject().toString())
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
