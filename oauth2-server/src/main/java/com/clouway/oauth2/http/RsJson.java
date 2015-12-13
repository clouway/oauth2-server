package com.clouway.oauth2.http;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsJson extends RsWrap {

  public RsJson(JsonStructure json) {
    this(new RsWithBody(RsJson.streaming(json)));
  }

  public RsJson(final Response res) {
    super(new RsWithType(res,
                    "application/json"
            )
    );
  }

  private static InputStream streaming(JsonStructure src) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final JsonWriter writer = Json.createWriter(baos);
    try {
      writer.write(src);
    } finally {
      writer.close();
    }
    return new ByteArrayInputStream(baos.toByteArray());
  }

}
