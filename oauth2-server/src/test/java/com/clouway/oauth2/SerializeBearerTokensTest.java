package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.RsPrint;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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
            "\"refresh_token\":" +
            "\"tGzv3JOkF0XG5Qx2TlKWIA\"}";

    assertThat(contentOf(new BearerTokenResponse("mF_9.B5f-4.1JqM", 3600L, "tGzv3JOkF0XG5Qx2TlKWIA")), is(equalTo(expectedResponse)));
  }

  @Test
  public void anotherToken() throws IOException {
    String expectedResponse = "Content-Type: application/json; charset=utf-8\r\n" +
            "{\"access_token\":\"::token2::\"," +
            "\"token_type\":\"Bearer\"," +
            "\"expires_in\":2400," +
            "\"refresh_token\":" +
            "\"::refresh_token::2\"}";

    assertThat(contentOf(new BearerTokenResponse("::token2::", 2400L, "::refresh_token::2")), is(equalTo(expectedResponse)));
  }

  private String contentOf(Response response) throws IOException {
    return new RsPrint(response).print();
  }

}