package com.clouway.oauth2.util;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.RequestExt;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class Params {
  public Map<String, String> parse(Request request, String... exclusions) {

    RequestExt req = (RequestExt) request;

    List<String> exclusionsList = Arrays.asList(exclusions);

    Map<String, String> params = Maps.newHashMap();

    for (String key : req.params().keySet()) {
      String value = request.param(key) == null ? "" : request.param(key);
      if (!Strings.isNullOrEmpty(value) && !exclusionsList.contains(key)) {
        params.put(key, value);
      }
    }

    return params;
  }
}
