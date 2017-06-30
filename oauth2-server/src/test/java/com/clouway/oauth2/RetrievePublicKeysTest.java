package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.RsPrint;
import com.google.common.base.Optional;
import org.junit.Test;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public class RetrievePublicKeysTest {

  Request request;

  @Test
  public void retrievePublicKeys() throws Exception {
    Map<String, String> keys = new TreeMap<String, String>() {{
      put("key1", "cert1");
      put("key2", "cert2");
      put("key3", "cert3");
    }};

    PublicCertsController controller = new PublicCertsController(new FakePublicKeys(keys));

    Response resp = controller.ack(request);
    RsPrint response = new RsPrint(resp);

    assertThat(response.printBody(), is("{\"key1\":\"cert1\",\"key2\":\"cert2\",\"key3\":\"cert3\"}"));
  }

  @Test
  public void publicKeysAreEmpty() throws Exception {
    PublicCertsController controller = new PublicCertsController(new FakePublicKeys(new HashMap<String, String>()));
    Response resp = controller.ack(request);
    RsPrint response = new RsPrint(resp);

    assertThat(response.printBody(), is(""));
  }

  private class FakePublicKeys implements PublicKeys {
    private final Map<String, String> keys;

    public FakePublicKeys(Map<String, String> keys) {
      this.keys = keys;
    }


    @Override
    public Optional<Key> getKey(String keyId) {
      String key = keys.get(keyId);
      try {
        EncodedKeySpec keySpec = new X509EncodedKeySpec(key.getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key pkey = keyFactory.generatePublic(keySpec);
        return Optional.of(pkey);

      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        return Optional.absent();
      }
    }

    @Override
    public Map<String, String> getKeys() {
      return keys;
    }
  }
}
