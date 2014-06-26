package com.example.auth.http;

import com.example.auth.core.ErrorResponseException;
import com.example.auth.core.Token;
import com.example.auth.core.TokenSecurity;
import com.example.auth.core.TokenRequest;
import com.google.common.collect.ImmutableMap;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.example.auth.http.ReplyMatchers.containsValue;
import static com.example.auth.http.ReplyMatchers.isBadRequest;
import static com.example.auth.http.ReplyMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenEndpointTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenSecurity tokenSecurity = context.mock(TokenSecurity.class);

  private TokenEndpoint endpoint = new TokenEndpoint(tokenSecurity);

  @Test
  public void happyPath() throws Exception {
    final TokenRequest tokenRequest = new TokenRequest("password", "FeNoMeNa", "parola");
    final Token token = new Token("token_value", "token_type");
    final TokenDTO tokenDTO = new TokenDTO("token_value", "token_type");
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "password", "username", "FeNoMeNa", "password", "parola"));

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).create(tokenRequest);
      will(returnValue(token));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isOk());
    assertThat(reply, containsValue(tokenDTO));
  }

  @Test
  public void errorResponse() throws Exception {
    final TokenRequest tokenRequest = new TokenRequest("password", "Ronaldo", "brazil");
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "password", "username", "Ronaldo", "password", "brazil"));
    final ErrorResponseDTO errorResponse = new ErrorResponseDTO("invalid_grant", "The username or password is incorrect!");

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).create(tokenRequest);
      will(throwException(new ErrorResponseException("invalid_grant", "The username or password is incorrect!")));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isBadRequest());
    assertThat(reply, containsValue(errorResponse));
  }
}