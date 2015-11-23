package com.clouway.oauth2.exampleapp;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.sitebricks.client.Transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class Json implements Transport {
  private Gson gson;

  @Inject
  Json(Gson gson) {
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
    gson.toJson(t, new OutputStreamWriter(outputStream, Charsets.UTF_8));
  }

  public String contentType() {
    return "application/json";
  }
}