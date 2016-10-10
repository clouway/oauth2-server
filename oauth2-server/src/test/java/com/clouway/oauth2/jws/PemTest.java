package com.clouway.oauth2.jws;

import com.clouway.oauth2.jws.Pem.Block;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

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

}