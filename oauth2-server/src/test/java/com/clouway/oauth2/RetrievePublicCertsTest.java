package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.keystore.IdentityKeyPair;
import com.clouway.oauth2.keystore.KeyStore;
import com.clouway.oauth2.util.PemKeyGenerator;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public class RetrievePublicCertsTest {

  @Test
  public void fewPublicCerts() throws Exception {
    final KeyPair keyPair = PemKeyGenerator.generatePair();

    PublicCertsController controller = new PublicCertsController(
            new FakeKeyStore(Arrays.asList(
                    new IdentityKeyPair("key1", keyPair.getPrivate(), keyPair.getPublic()),
                    new IdentityKeyPair("key2", keyPair.getPrivate(), keyPair.getPublic()),
                    new IdentityKeyPair("key3", keyPair.getPrivate(), keyPair.getPublic())
            ))
    );

    Response resp = controller.ack(null);
    JsonObject response = new RsPrint(resp).asJson();

    Pem pem = new Pem();
    String firstPemKey = response.get("key1").getAsString();
    String secondPemKey = response.get("key2").getAsString();
    String thirdPemKey = response.get("key3").getAsString();

    pem.parse(new ByteArrayInputStream(firstPemKey.getBytes()));
    pem.parse(new ByteArrayInputStream(secondPemKey.getBytes()));
    pem.parse(new ByteArrayInputStream(thirdPemKey.getBytes()));
  }

  @Test
  public void noPublicCerts() throws Exception {
    PublicCertsController controller = new PublicCertsController(new FakeKeyStore(Collections.<IdentityKeyPair>emptyList()));
    Response resp = controller.ack(null);
    RsPrint response = new RsPrint(resp);

    assertThat(response.printBody(), is("{}"));
  }

  private class FakeKeyStore implements KeyStore {
    private final List<IdentityKeyPair> keys;

    public FakeKeyStore(List<IdentityKeyPair> keys) {
      this.keys = keys;
    }


    @Override
    public List<IdentityKeyPair> getKeys() {
      return keys;
    }
  }
}
