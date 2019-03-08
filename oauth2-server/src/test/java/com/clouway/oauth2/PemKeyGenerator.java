package com.clouway.oauth2;

import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class PemKeyGenerator {
  private static KeyPair keyPair = getKeyPair(2048);

  public static KeyPair generatePair() {
    return getKeyPair(2048);
  }

  public static Pem.Block generatePublicKey() {
    PublicKey publicKey = keyPair.getPublic();
    return new Pem.Block("PUBLIC KEY", Collections.<String, String>emptyMap(), publicKey.getEncoded());
  }

  public static Pem.Block generatePrivateKey() {
    PrivateKey privateKey = keyPair.getPrivate();
    return new Pem.Block("PRIVATE KEY", Collections.<String, String>emptyMap(), privateKey.getEncoded());
  }

  private static KeyPair getKeyPair(Integer size) {
    KeyPairGenerator keyGen = null;
    try {
      keyGen = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    keyGen.initialize(size);
    return keyGen.generateKeyPair();
  }
}
