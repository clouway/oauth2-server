package com.clouway.oauth2.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsJson extends RsWrap {

  public static final Gson GSON = new Gson();

  public RsJson(JsonElement json) {
    this(new RsWithBody(RsJson.streaming(json)));
  }

  public RsJson(final Response res) {
    super(new RsWithType(res,
                    "application/json"
            )
    );
  }

  private static InputStream streaming(JsonElement src) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(baos))) {
      GSON.toJson(src, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ByteArrayInputStream(baos.toByteArray());
  }

}
