package com.clouway.oauth2.http;

import com.clouway.oauth2.ByteRequest;
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
public class RequiresHeaderTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  Fork origin;

  @Test
  public void headerIsProvided() throws IOException {

    final Request request = new ByteRequest(
            ImmutableMap.<String, String>of(),
            ImmutableMap.of("Authorization", "aaaa"));

    context.checking(new Expectations() {{
      oneOf(origin).route(request);
      will(returnValue(Optional.of(new RsText("::some_body::"))));
    }});


    Optional<Response> response = new RequiresHeader("Authorization", origin).route(request);
    assertThat(response.isPresent(), is(true));
    assertThat(new RsPrint(response.get()).printBody(), is(equalTo("::some_body::")));
  }

  @Test
  public void headerIsEmpty() throws IOException {
    try {
      new RequiresHeader("Authorization", origin).route(new ByteRequest(
              ImmutableMap.<String, String>of(),
              ImmutableMap.of("Authorization", "")));
      fail("not http exception was thrown when header is empty?");
    } catch (HttpException e) {
      assertThat(e.code(), is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
    }

  }

  @Test
  public void headerIsNotProvided() throws IOException {
    try {
      new RequiresHeader("Authorization", origin).route(new ParamRequest(ImmutableMap.<String, String>of()));
      fail("not http exception was thrown when header is not provided?");
    } catch (HttpException e) {
      assertThat(e.code(), is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
    }

  }
}
