package com.example.auth.app;

import com.example.auth.core.token.TokenErrorResponse;
import com.example.auth.core.token.Token;
import com.example.auth.core.token.TokenSecurity;
import com.example.auth.core.token.TokenRequest;
import com.google.common.collect.ImmutableMap;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.example.auth.app.ReplyMatchers.containsValue;
import static com.example.auth.app.ReplyMatchers.isBadRequest;
import static com.example.auth.app.ReplyMatchers.isOk;
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
    final TokenRequest tokenRequest = new TokenRequest("grant_type", "code", "client_id", "client_secret");
    final Token token = new Token("token_value", "token_type");
    final TokenDTO tokenDTO = new TokenDTO("token_value", "token_type");
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "grant_type", "code", "code", "client_id", "client_id", "client_secret", "client_secret"));

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
    final TokenRequest tokenRequest = new TokenRequest("auth_code", "1234", "client123", "secret123");
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "auth_code", "code", "1234", "client_id", "client123", "client_secret", "secret123"));
    final ErrorResponseDTO errorResponse = new ErrorResponseDTO("invalid_grant", "The username or password is incorrect!");

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).create(tokenRequest);
      will(throwException(new TokenErrorResponse("invalid_grant", "The username or password is incorrect!")));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isBadRequest());
    assertThat(reply, containsValue(errorResponse));
  }
}