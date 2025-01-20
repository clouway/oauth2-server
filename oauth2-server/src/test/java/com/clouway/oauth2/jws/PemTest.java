package com.clouway.oauth2.jws;

import com.clouway.oauth2.jws.Pem.Block;
import com.google.common.collect.ImmutableMap;
import kotlin.text.Charsets;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class PemTest {

  @Test
  public void decodeWhatWasEncodedInPemFormat() throws Exception {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(2048);

    KeyPair keyPair = keyGen.generateKeyPair();
    PrivateKey privateKey = keyPair.getPrivate();
    byte[] keyAsBytes = privateKey.getEncoded();

    Pem pem = new Pem();
    String encoded = pem.format(new Pem.Block("RSA PRIVATE KEY", ImmutableMap.<String, String>of(), keyAsBytes));
    Block block = pem.parse(new ByteArrayInputStream(encoded.getBytes()));

    assertThat(block, is(equalTo(new Block("RSA PRIVATE KEY", ImmutableMap.<String, String>of(), keyAsBytes))));
  }

  @Test
  public void testSomething() throws IOException {
    String s = "-----BEGIN RSA PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDg+lXtSGpMfXWY\nPbaVy5vIzAObh6zryGTleoSJvMXTl/qIo55tHohOkvbUyJyLwjptjjwIkHD7hMbZ\nvDLYstwbdWDbc78gCt7cIDuupS0LSsEeTev1fca8bOjbU82/YFPnzFrXLozZLWN/\nPrQ9fGpY/vhd8GBEdAtMLBqPC6DVKuYqoajtazQczUiINDQ82+BnDQBPL9JzxbS/\nY68mupQRTQgzLrn0OypjPanS09vL4umXdnPTgzMXwi9HVY+nIgJkBNA/0pxLgkUT\nFS7iJJK0SUR44Hb2k+4RIMqievvkLhl7ngr+Bv76wj+wmjepjBOvZBNi0qeS9kxM\nMWcXtaORAgMBAAECggEAegUsf5KsHKpbEmQ+WEZjSufj+QIANq2sk438vpvFC/s8\n5ckgTvylX0B0YXog9eg1OmBPjTGSJcig9U0Oott3Z+kkzEGd4qoir29ID3QE10AH\nOAF5Pd+m32GOdg6g4/BB8dGzX0wfFld4xHXk2ghVwckgOgr6m4X47qppCTZ8V8B1\nCqvS5FYdHWRanGo64znGPx6bC5n/xMLr3NhlRbixrVdHT+JA4Cvk4Kantn9OZcZR\nI/LQjqZ49Rd9/gX0u3d+vY2NPY1N2QWV0MSPgequTMDyJz9lXJr7OuT+fQJ+RRcQ\nnepVpT2C8qbzYolxsLMs/sxIYZjvpKTBoaVtxMf2FQKBgQDka1OiOh2Bc1zIv2V0\npVIjue/1H4jJOgaLZeQYOC+GJdRE43kEe0OdW/WGozf5NyAszlQNSRigrZMPaUig\njmlHI9trJXeMlnowxGg7Fsfx9OhUGmfkLDry1xtjk7oILi5eRZz3SAzImNidHV7d\nl221b+HUcQDuIFrPyR0JaTDYKwKBgQD8JKH0saPIJGfHADUlbyEYAAaJK/FsnA7i\ne3Rh2BDH4s7RC2/I+Q0tth/PX6PNDaaQM9lJupHP368IqmPUW/OxoNOiI+xlyr4t\n6mPrsEQaY3htACmIYiFTDOfBfNaqF/tMALzPo6lGALLrjt5bL6JKuwB4IWTvM0TV\n4WX4aws5MwKBgDRZlLVddF2yvtUTaIEvUn/1oVUggQz9S3qvQ3N5jQrFqLyRFa89\nQOXTqZXN2oo3ZBxgvUq+MfLBVS73Bjol6WLwiN0pnRiPdDmxCeJg+jot0wFTe/QD\nXw9A1Xog5UXyr5XThoH19VgUD7EShidrCS3IEo3JyFjK+YUdppX9kcA5AoGAL2ok\nGoOdLPHLohxj4ho3uu+mSv08dRQTqHtWs1+SKER6Z80ixEQxOjtZWAHAJ7s9aziU\nz8yJxvFlVNfV1gVEmk6H/aGLvsiVYsUE7TlEVUIHT1gMd10cryVqH3R+WZYQ54Xr\n+4/nMQbInotLPRKEDlGEERMWi/S0KRQtvL1EawkCgYAJn/P2B5pYe7cqGBBawB++\nAVMFluYIAFHgHkQh/3Ge9JQAM7bBSbXW2LpqiinAuUf5ZxK4UQ9yEwO4qiW16GJw\n2gl2LMxCHk9my95lYbZGS3YZV6D1/NmMbBJFpr8AYSSbgNhn9kCRy6e2+79x61mV\nvZQADgzirUFJTt4xSIexMg==\n-----END RSA PRIVATE KEY-----\n";
    Pem pem = new Pem();

    Pem.Block block = pem.parse(new ByteArrayInputStream(s.getBytes()));
    System.out.println(Arrays.toString(block.getBytes()));
  }

  @Test
  public void decode() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
    String payload = "-----BEGIN RSA PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDg+lXtSGpMfXWY\nPbaVy5vIzAObh6zryGTleoSJvMXTl/qIo55tHohOkvbUyJyLwjptjjwIkHD7hMbZ\nvDLYstwbdWDbc78gCt7cIDuupS0LSsEeTev1fca8bOjbU82/YFPnzFrXLozZLWN/\nPrQ9fGpY/vhd8GBEdAtMLBqPC6DVKuYqoajtazQczUiINDQ82+BnDQBPL9JzxbS/\nY68mupQRTQgzLrn0OypjPanS09vL4umXdnPTgzMXwi9HVY+nIgJkBNA/0pxLgkUT\nFS7iJJK0SUR44Hb2k+4RIMqievvkLhl7ngr+Bv76wj+wmjepjBOvZBNi0qeS9kxM\nMWcXtaORAgMBAAECggEAegUsf5KsHKpbEmQ+WEZjSufj+QIANq2sk438vpvFC/s8\n5ckgTvylX0B0YXog9eg1OmBPjTGSJcig9U0Oott3Z+kkzEGd4qoir29ID3QE10AH\nOAF5Pd+m32GOdg6g4/BB8dGzX0wfFld4xHXk2ghVwckgOgr6m4X47qppCTZ8V8B1\nCqvS5FYdHWRanGo64znGPx6bC5n/xMLr3NhlRbixrVdHT+JA4Cvk4Kantn9OZcZR\nI/LQjqZ49Rd9/gX0u3d+vY2NPY1N2QWV0MSPgequTMDyJz9lXJr7OuT+fQJ+RRcQ\nnepVpT2C8qbzYolxsLMs/sxIYZjvpKTBoaVtxMf2FQKBgQDka1OiOh2Bc1zIv2V0\npVIjue/1H4jJOgaLZeQYOC+GJdRE43kEe0OdW/WGozf5NyAszlQNSRigrZMPaUig\njmlHI9trJXeMlnowxGg7Fsfx9OhUGmfkLDry1xtjk7oILi5eRZz3SAzImNidHV7d\nl221b+HUcQDuIFrPyR0JaTDYKwKBgQD8JKH0saPIJGfHADUlbyEYAAaJK/FsnA7i\ne3Rh2BDH4s7RC2/I+Q0tth/PX6PNDaaQM9lJupHP368IqmPUW/OxoNOiI+xlyr4t\n6mPrsEQaY3htACmIYiFTDOfBfNaqF/tMALzPo6lGALLrjt5bL6JKuwB4IWTvM0TV\n4WX4aws5MwKBgDRZlLVddF2yvtUTaIEvUn/1oVUggQz9S3qvQ3N5jQrFqLyRFa89\nQOXTqZXN2oo3ZBxgvUq+MfLBVS73Bjol6WLwiN0pnRiPdDmxCeJg+jot0wFTe/QD\nXw9A1Xog5UXyr5XThoH19VgUD7EShidrCS3IEo3JyFjK+YUdppX9kcA5AoGAL2ok\nGoOdLPHLohxj4ho3uu+mSv08dRQTqHtWs1+SKER6Z80ixEQxOjtZWAHAJ7s9aziU\nz8yJxvFlVNfV1gVEmk6H/aGLvsiVYsUE7TlEVUIHT1gMd10cryVqH3R+WZYQ54Xr\n+4/nMQbInotLPRKEDlGEERMWi/S0KRQtvL1EawkCgYAJn/P2B5pYe7cqGBBawB++\nAVMFluYIAFHgHkQh/3Ge9JQAM7bBSbXW2LpqiinAuUf5ZxK4UQ9yEwO4qiW16GJw\n2gl2LMxCHk9my95lYbZGS3YZV6D1/NmMbBJFpr8AYSSbgNhn9kCRy6e2+79x61mV\nvZQADgzirUFJTt4xSIexMg==\n-----END RSA PRIVATE KEY-----\n";
    Pem pem = new Pem();
    Block block = pem.parse(new ByteArrayInputStream(payload.getBytes()));

    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(block.getBytes());
    KeyFactory kf = KeyFactory.getInstance("RSA");

    PrivateKey privKey = kf.generatePrivate(keySpec);

    java.security.Signature sig = Signature.getInstance("SHA256withRSA");
    sig.initSign(privKey);


    String message = "Hello, secure world!";
    sig.update(message.getBytes(Charsets.UTF_8));

    byte[] checkSign = sig.sign();


    System.out.println("signature:" + new String(checkSign));
    
    assertThat(Base64.getEncoder().encodeToString(checkSign), is(equalTo("gd2kdJqIdtQl/ZalTJJegyrpEp1lslqhWuWbKQ0YhEJz+Rasvmur9xKcza/LPcQmzRVWHBUrLlJ+AT4JWArLf1trLpipLxo7cq9DTx2cU56v++tn/04m8ypmCREfitcejYS97x1oQKkMXnj90HfTo8yRNmrMZnwFxxrRZZgu3zJFNzTswsJi/xobXJYJ61/EkaURPc3TT/koEXvzFx2Urn/5D/D4eCe+UzPd0J+tAdhZK8LztFEO+do81xlpJ39DapafUO2qunnmj5gGy7JUuZH4b0AVY/auv7SEUvAsOstl6db8SzXPX+vhUQDAKy0V3lRErVJWyVZAdvYdwjLHKA==")));
  }
}