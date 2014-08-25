package com.example.auth.app;

import com.example.auth.core.token.Token;
import com.example.auth.core.token.TokenCreator;
import com.example.auth.core.token.TokenErrorResponse;
import com.example.auth.core.token.TokenRequest;
import com.example.auth.core.token.TokenSecurity;
import com.example.auth.core.token.refreshtoken.RefreshToken;
import com.example.auth.core.token.refreshtoken.RefreshTokenProvider;
import com.google.common.collect.ImmutableMap;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

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

  @Mock
  private RefreshTokenProvider refreshTokenProvider;

  @Mock
  private TokenCreator tokenCreator;

  private TokenEndpoint endpoint;

  @Before
  public void setUp() throws Exception {
    endpoint = new TokenEndpoint(tokenSecurity, refreshTokenProvider, tokenCreator);
  }

  @Test
  public void generatingToken() throws Exception {
    final TokenRequest tokenRequest = new TokenRequest("grant_type", "code", "refresh_token", "client_id", "client_secret");
    final Token token = new Token("token_value", "token_type", "userId", 0l, new Date());
    final TokenDTO tokenDTO = new TokenDTO("token_value", "refresh_value", "token_type", 0l);
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "grant_type", "code", "code", "client_id", "client_id", "client_secret", "client_secret", "refresh_token", "refresh_token"));


    final RefreshToken refreshToken = new RefreshToken("clientId", "", "refresh_value");

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).validateAuthCode(tokenRequest);

      oneOf(refreshTokenProvider).provide(tokenRequest.refreshToken, tokenRequest.clientId, tokenRequest.clientSecret);
      will(returnValue(refreshToken));

      oneOf(tokenCreator).create(tokenRequest.code);
      will(returnValue(token));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isOk());
    assertThat(reply, containsValue(tokenDTO));
  }

  @Test
  public void generatingTokenUsingRefreshToken() throws Exception {
    final TokenRequest tokenRequest = new TokenRequest("refresh_token", "code", "refresh_token_value", "client_id", "client_secret");
    final Token token = new Token("token_value", "token_type", "userId", 0l, new Date());
    final TokenDTO tokenDTO = new TokenDTO("token_value", "refresh_value", "token_type", 0l);
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "refresh_token", "code", "code", "client_id", "client_id", "client_secret", "client_secret", "refresh_token", "refresh_token_value"));


    final RefreshToken refreshToken = new RefreshToken("", "", "refresh_value");

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).validateRefreshToken(tokenRequest);

      oneOf(refreshTokenProvider).provide(tokenRequest.refreshToken, tokenRequest.clientId, tokenRequest.clientSecret);
      will(returnValue(refreshToken));

      oneOf(tokenCreator).create(tokenRequest.code);
      will(returnValue(token));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isOk());
    assertThat(reply, containsValue(tokenDTO));
  }

  @Test
  public void errorResponse() throws Exception {
    final TokenRequest tokenRequest = new TokenRequest("auth_code", "1234", "refreshToken", "client123", "secret123");
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "auth_code", "code", "1234", "client_id", "client123", "client_secret", "secret123"));
    final ErrorResponseDTO errorResponse = new ErrorResponseDTO("invalid_grant", "The username or password is incorrect!");

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).validateAuthCode(tokenRequest);
      will(throwException(new TokenErrorResponse("invalid_grant", "The username or password is incorrect!")));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isBadRequest());
    assertThat(reply, containsValue(errorResponse));
  }

  @Test
  public void errorResponseWhenValidatingRefreshToken() throws Exception {
    final TokenRequest tokenRequest = new TokenRequest("refresh_token", "1234", "refreshToken", "client123", "secret123");
    final Request request = new ParameterRequest(ImmutableMap.of("grant_type", "refresh_token", "code", "1234", "client_id", "client123", "client_secret", "secret123"));
    final ErrorResponseDTO errorResponse = new ErrorResponseDTO("invalid_grant", "Erooooor");

    context.checking(new Expectations() {{
      oneOf(tokenSecurity).validateRefreshToken(tokenRequest);
      will(throwException(new TokenErrorResponse("invalid_grant", "Erooooor")));
    }});

    Reply<?> reply = endpoint.generate(request);

    assertThat(reply, isBadRequest());
    assertThat(reply, containsValue(errorResponse));
  }
}