package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.RsPrint;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class SerializeBearerTokensTest {

  @Test
  public void happyPath() throws IOException {

    String expectedResponse = "Content-Type: application/json; charset=utf-8\r\n" +
            "{\"access_token\":\"mF_9.B5f-4.1JqM\"," +
            "\"token_type\":\"Bearer\"," +
            "\"expires_in\":3600," +
            "\"scope\":\"scope1 scope2\"," +
            "\"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\"," +
            "\"id_token\":\"::id token::\"}";

    assertThat(contentOf(new BearerTokenResponse("mF_9.B5f-4.1JqM", 3600L, Sets.newTreeSet(Arrays.asList("scope1", "scope2")), "tGzv3JOkF0XG5Qx2TlKWIA", "::id token::")), is(equalTo(expectedResponse)));
  }

  @Test
  public void anotherToken() throws IOException {

    String expectedResponse = "Content-Type: application/json; charset=utf-8\r\n" +
            "{\"access_token\":\"::token2::\"," +
            "\"token_type\":\"Bearer\"," +
            "\"expires_in\":2400," +
            "\"refresh_token\":" +
            "\"::refresh_token::2\"," +
            "\"id_token\":\"::id token::\"}";

    assertThat(contentOf(new BearerTokenResponse("::token2::", 2400L, Collections.<String>emptySet(), "::refresh_token::2", "::id token::")), is(equalTo(expectedResponse)));
  }

  private String contentOf(Response response) throws IOException {
    return new RsPrint(response).print();
  }

}