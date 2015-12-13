package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.google.common.collect.Maps;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class Requests {

  public static Request newRequest(final String path, List<String> params) {
    final Map<String, String> pmap = Maps.newHashMap();
    for (String each : params) {

      if (!each.contains(" ")) {
        throw new IllegalArgumentException("List items should be space delimited.");
      }
      String[] parts = each.split("\\s");
      pmap.put(parts[0], parts[1]);
    }

    return new Request() {
      @Override
      public String path() {
        return path;
      }

      @Override
      public String param(String key) {
        return pmap.get(key);
      }

      @Override
      public String header(String name) {
        return null;
      }

      @Override
      public InputStream body() {
        return null;
      }

    };
  }

  public static Request newRequest(final String path) {
    return new Request() {

      @Override
      public String path() {
        return path;
      }

      @Override
      public String param(String key) {
        return null;
      }

      @Override
      public String header(String name) {
        return null;
      }

      @Override
      public InputStream body() {
        return null;
      }
    };
  }
}
