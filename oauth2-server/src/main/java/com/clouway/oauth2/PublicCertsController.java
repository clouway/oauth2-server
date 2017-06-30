package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsEmpty;
import com.clouway.friendlyserve.RsJson;
import com.clouway.friendlyserve.Take;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

/**
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public class PublicCertsController implements Take {
  private final PublicKeys keys;

  public PublicCertsController(PublicKeys keys) {
    this.keys = keys;
  }

  @Override
  public Response ack(Request request) throws IOException {
    Map<String, String> keyMap = keys.getKeys();
    if (keyMap.isEmpty()) {
      return new RsEmpty();
    }

    JsonObject o = new JsonObject();
    for (String each : keyMap.keySet()) {
      o.addProperty(each, keyMap.get(each));
    }

    return new RsJson(o);
  }
}
