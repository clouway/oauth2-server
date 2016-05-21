package com.clouway.oauth2.http;

import com.google.common.base.Optional;
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
  Fork origin;

  @Test
  public void paramIsPassed() throws IOException {
    final Request anyRequestWithProvidedParam = new ParamRequest(ImmutableMap.of("assertion", "::assertion::"));

    context.checking(new Expectations() {{
      oneOf(origin).route(anyRequestWithProvidedParam);
      will(returnValue(Optional.of(new RsText("::body::"))));
    }});

    Optional<Response> response = new RequiresParam("assertion", origin).route(anyRequestWithProvidedParam);
    assertThat(response.isPresent(), is(true));
    assertThat(new RsPrint(response.get()).printBody(), is(equalTo("::body::")));
  }

  @Test
  public void emptyParam() throws IOException {
    try {
      new RequiresParam("assertion", origin).route(new ParamRequest(ImmutableMap.of("assertion", "")));
      fail("no exception was thrown when empty param was provided?");
    } catch (HttpException e) {
      assertThat(e.code(), is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
    }

  }

  @Test
  public void noParam() throws IOException {
    try {
      new RequiresParam("assertion", origin).route(new ParamRequest(ImmutableMap.<String, String>of()));
      fail("no exception was thrown when no param was provided?");
    } catch (HttpException e) {
      assertThat(e.code(), is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
    }
  }

}