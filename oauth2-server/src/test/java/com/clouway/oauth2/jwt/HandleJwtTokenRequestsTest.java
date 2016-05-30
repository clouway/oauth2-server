package com.clouway.oauth2.jwt;

import com.clouway.oauth2.client.ServiceAccount;
import com.clouway.oauth2.client.ServiceAccountRepository;
import com.clouway.oauth2.http.ParamRequest;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsPrint;
import com.clouway.oauth2.jws.Signature;
import com.clouway.oauth2.jws.SignatureFactory;
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
import java.util.Date;

import static com.clouway.oauth2.util.Matchers.matching;
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
  ServiceAccountRepository repository;

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

    final byte[] signatureAsBytes = {
            101, 121, 74, 104, 98, 71, 99, 105, 79, 105, 74, 83, 85, 122, 73, 49, 78, 105, 73, 115, 73, 110, 82, 53, 99,
            67, 73, 54, 73, 107, 112, 88, 86, 67, 74, 57, 46, 101, 121, 74, 112, 99, 51, 77, 105, 79, 105, 74, 52, 101,
            72, 104, 65, 90, 71, 86, 50, 90, 87, 120, 118, 99, 71, 86, 121, 76, 109, 78, 118, 98, 83, 73, 115, 73, 110,
            78, 106, 98, 51, 66, 108, 73, 106, 111, 105, 100, 71, 86, 122, 100, 68, 69, 103, 100, 71, 86, 122, 100, 68,
            73, 105, 76, 67, 74, 104, 100, 87, 81, 105, 79, 105, 74, 111, 100, 72, 82, 119, 79, 105, 56, 118, 98, 71,
            57, 106, 89, 87, 120, 111, 98, 51, 78, 48, 79, 106, 107, 119, 77, 68, 73, 118, 98, 50, 70, 49, 100, 71, 103,
            121, 76, 51, 82, 118, 97, 50, 86, 117, 73, 105, 119, 105, 90, 88, 104, 119, 73, 106, 111, 120, 78, 68, 89,
            120, 77, 106, 77, 52, 79, 84, 81, 52, 76, 67, 74, 112, 89, 88, 81, 105, 79, 106, 69, 48, 78, 106, 69, 121,
            77, 122, 85, 122, 78, 68, 103, 115, 73, 110, 78, 49, 89, 105, 73, 54, 73, 110, 86, 122, 90, 88, 74, 65, 90,
            88, 104, 104, 98, 88, 66, 115, 90, 83, 53, 106, 98, 50, 48, 105, 76, 67, 74, 119, 99, 109, 52, 105, 79, 105,
            74, 49, 99, 50, 86, 121, 81, 71, 86, 52, 89, 87, 49, 119, 98, 71, 85, 117, 89, 50, 57, 116, 73, 110, 48
    };

    final Signature anySignatureThatWillVerifies = context.mock(Signature.class);
    final Date anyInstantTime = new Date();

    context.checking(new Expectations() {{
      oneOf(repository).findServiceAccount(with(any(Jwt.ClaimSet.class)));
      will(returnValue(Optional.of(new ServiceAccount("::client_id::", "::private_key::"))));

      oneOf(signatureFactory).createSignature(with(any(byte[].class)), with(any(Jwt.Header.class)));
      will(returnValue(Optional.of(anySignatureThatWillVerifies)));

      oneOf(anySignatureThatWillVerifies).verify(with(signatureAsBytes), with(matching("::private_key::")));
      will(returnValue(true));

      oneOf(tokens).issueToken("::client_id::", anyInstantTime);
      will(returnValue(new Token("::token_value::", TokenType.BEARER, "::refresh_token::", "::client_id::", 1000L, new Date())));
    }});

    Response response = controller.handleAsOf(newJwtRequest(String.format("%s.%s.%s", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9", body, signature)),anyInstantTime);
    String responseContent = new RsPrint(response).printBody();
    assertThat(responseContent, containsString("::token_value::"));
    assertThat(responseContent, containsString("::refresh_token::"));
    assertThat(responseContent, containsString("1000"));
  }

  @Test
  public void assertionIsEmpty() throws IOException {
    Response response = controller.handleAsOf(newJwtRequest(""), new Date());
    assertThat(new RsPrint(response).print(), containsString("invalid_request"));
  }

  @Test
  public void headerIsMissing() throws IOException {
    Response response = controller.handleAsOf(newJwtRequest(String.format("%s.%s", body, signature)), new Date());
    assertThat(new RsPrint(response).printBody(), containsString("invalid_request"));
  }

  private Request newJwtRequest(String assertion) {
    return new ParamRequest(ImmutableMap.of("assertion", assertion));
  }
}