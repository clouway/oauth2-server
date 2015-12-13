package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.ParamRequest;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsPrint;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RefreshTokenForClientTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  TokenRepository tokenRepository;

  @Test
  public void happyPath() throws IOException {
    RefreshTokenActivity action = new RefreshTokenActivity(tokenRepository);
    Client client = aNewClient().withId("client1").withSecret("secret1").build();

    context.checking(new Expectations() {{
      oneOf(tokenRepository).refreshToken("::refresh_token::");
      will(returnValue(Optional.of(new Token("::token1::", "berier", "::refresh_token::", "", 600L, new Date()))));
    }});

    Response response = action.execute(client, new ParamRequest(ImmutableMap.of("refresh_token", "::refresh_token::")));

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::token1::"));
    assertThat(body, containsString("600"));
    assertThat(body, containsString("::refresh_token::"));
  }

  @Test
  public void refreshTokenWasExpired() throws IOException {
    RefreshTokenActivity action = new RefreshTokenActivity(tokenRepository);
    Client client = aNewClient().withId("client1").withSecret("secret1").build();

    context.checking(new Expectations() {{
      oneOf(tokenRepository).refreshToken("::refresh_token::");
      will(returnValue(Optional.absent()));
    }});

    Response response = action.execute(client, new ParamRequest(ImmutableMap.of("refresh_token", "::refresh_token::")));

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("invalid_grant"));
  }

}