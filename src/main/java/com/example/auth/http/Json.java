package com.example.auth.http;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.sitebricks.client.Transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class Json implements Transport {
  private Gson gson;

  @Inject
  public Json(Gson gson) {
    this.gson = gson;
  }

  public <T> T in(InputStream inputStream, Class<T> tClass) throws IOException {
    return gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), tClass);
  }

  @Override
  public <T> T in(InputStream inputStream, TypeLiteral<T> typeLiteral) throws IOException {
    return gson.fromJson(new InputStreamReader(inputStream,"UTF-8"), typeLiteral.getType());
  }

  public <T> void out(OutputStream outputStream, Class<T> tClass, T t) {
    String json = gson.toJson(t);
    try {
      outputStream.write(json.getBytes("UTF8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String contentType() {
    return "application/json";
  }
}