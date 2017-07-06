package com.clouway.oauth2.util;

import com.clouway.friendlyserve.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Map;

import static com.clouway.friendlyserve.testing.FakeRequest.aNewRequest;
import static com.google.common.collect.ImmutableMap.of;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ParamsTest {
  @Test
  public void parse() throws Exception {
    Map<String, String> params = new Params().parse(aNewRequest().param("::index::", "::1::").build());
    assertThat(params, CoreMatchers.<Map<String, String>>is(of("::index::", "::1::")));
  }

  @Test
  public void excludeParams() throws Exception {
    Request request = aNewRequest().param("::i::", "::1::").param("::codeparam::", "::1::").param("::otherparam::", "::1::").build();
    Map<String, String> params = new Params().parse(request, "::codeparam::", "::otherparam::");
    assertThat(params, CoreMatchers.<Map<String, String>>is(of("::i::", "::1::")));
  }

}