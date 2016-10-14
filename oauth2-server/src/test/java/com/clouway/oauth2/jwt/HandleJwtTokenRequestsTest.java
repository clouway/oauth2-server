package com.clouway.oauth2.jwt;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Signature;
import com.clouway.oauth2.jws.SignatureFactory;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenType;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class HandleJwtTokenRequestsTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private final String body = "eyJpc3MiOiJ4eHhAZGV2ZWxvcGVyLmNvbSIsInNjb3BlIjoidGVzdDEgdGVzdDIiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjkwMDIvb2F1dGgyL3Rva2VuIiwiZXhwIjoxNDYxMjM4OTQ4LCJpYXQiOjE0NjEyMzUzNDgsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJwcm4iOiJ1c2VyQGV4YW1wbGUuY29tIn0";
  private final String signature = "WBAzzss3J8Ea6-xxOCVS2OZ2HoqpiLdfCLhIJEevaPck377qTpiM__lHta_S8dSCuTl5FjREqixIiwGrJVJEIkfExUwS5YWekdJRniSKdqLjmXussePaCSgco3reJDqNcRCGiv9DSLH0GfZFdv11Ik5nyaHjNnS4ykEi76guaY8-T3uVFjOH4e2o8Wm0vBbq9hzo9UHdgnsI2BLrzDVoydGWM7uZW8MQNKTuGWY_Ywyj1hilr9rw4yy2FvBe7G-56qaq8--IlVNZ6ocJX2dYhZPqDtZUYwLRqwFyM_F53Kt81I8Qht6HBgH-fgrfbd7Ms67BeLGsupFvuM9sF-hGOQ";

  @Mock
  ClientKeyStore repository;

  @Mock
  Tokens tokens;

  @Mock
  SignatureFactory signatureFactory;

  private JwtController controller;

  @Before
  public void setUp() {
    controller = new JwtController(signatureFactory, tokens, repository);
  }

  @Test
  public void happyPath() throws IOException {
    final Signature anySignatureThatWillVerifies = context.mock(Signature.class);
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(repository).findKey(with(any(Jwt.Header.class)), with(any(Jwt.ClaimSet.class)));
      will(returnValue(Optional.of(new Pem.Block("", ImmutableMap.<String, String>of(), new byte[]{}))));

      oneOf(signatureFactory).createSignature(with(any(byte[].class)), with(any(Jwt.Header.class)));
      will(returnValue(Optional.of(anySignatureThatWillVerifies)));

      oneOf(anySignatureThatWillVerifies).verify(with(any(byte[].class)), with(any(Pem.Block.class)));
      will(returnValue(true));

      oneOf(tokens).issueToken(with(any(GrantType.class)), with(any(Client.class)), with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(new Token("::token_value::", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "::refresh_token::", "::client_id::", "::client_id::", 1000L, new DateTime())));
    }});

    Response response = controller.handleAsOf(newJwtRequest(String.format("%s.%s.%s", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9", body, signature)), anyInstantTime);
    String responseContent = new RsPrint(response).printBody();
    assertThat(responseContent, containsString("::token_value::"));
    assertThat(responseContent, containsString("::refresh_token::"));
    assertThat(responseContent, containsString("1000"));
  }

  @Test
  public void assertionIsEmpty() throws IOException {
    Response response = controller.handleAsOf(newJwtRequest(""), new DateTime());
    assertThat(new RsPrint(response).print(), containsString("invalid_request"));
  }

  @Test
  public void headerIsMissing() throws IOException {
    Response response = controller.handleAsOf(newJwtRequest(String.format("%s.%s", body, signature)), new DateTime());
    assertThat(new RsPrint(response).printBody(), containsString("invalid_request"));
  }

  private Request newJwtRequest(String assertion) {
    return new ParamRequest(ImmutableMap.of("assertion", assertion));
  }
}