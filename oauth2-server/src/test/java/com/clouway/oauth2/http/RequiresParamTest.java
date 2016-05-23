package com.clouway.oauth2.http;

import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RequiresParamTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  Take origin;

  @Test
  public void paramIsPassed() throws IOException {
    final Request anyRequestWithProvidedParam = new ParamRequest(ImmutableMap.of("assertion", "::assertion::"));

    context.checking(new Expectations() {{
      oneOf(origin).ack(anyRequestWithProvidedParam);
      will(returnValue(new RsText("::origin_body::")));
    }});

    Response response = new RequiresParam("assertion", origin).ack(anyRequestWithProvidedParam);
    assertThat(new RsPrint(response).printBody(), is(equalTo("::origin_body::")));
  }

  @Test
  public void emptyParam() throws IOException {
    Response response = new RequiresParam("assertion", origin).ack(new ParamRequest(ImmutableMap.of("assertion", "")));

    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
  }

  @Test
  public void noParam() throws IOException {
    Response response = new RequiresParam("assertion", origin).ack(new ParamRequest(ImmutableMap.<String, String>of()));
    assertThat(new RsPrint(response).print(), equalTo(new RsPrint(new RsBadRequest()).print()));
  }

}