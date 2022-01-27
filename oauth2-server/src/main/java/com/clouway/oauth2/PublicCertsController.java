package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsJson;
import com.clouway.friendlyserve.Take;
import com.clouway.oauth2.keystore.IdentityKeyPair;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.keystore.KeyStore;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public class PublicCertsController implements Take {
  private final KeyStore keyStore;

  public PublicCertsController(KeyStore keyStore) {
    this.keyStore = keyStore;
  }

  @Override
  public Response ack(Request request) throws IOException {
    List<IdentityKeyPair> keyMap = keyStore.getKeys();

    JsonObject o = new JsonObject();
    if (keyMap.isEmpty()) {
      return new RsJson(o);
    }

    Pem pem = new Pem();

    for (IdentityKeyPair each : keyMap) {
      String certificateAsText = pem.format(
              new Block("CERTIFICATE", Collections.<String, String>emptyMap(), each.publicKey.getEncoded())
      );
      o.addProperty(each.keyId, certificateAsText);
    }

    return new RsJson(o);
  }
}
