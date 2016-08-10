package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.Status;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.gson.JsonObject;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.Collections;

import static com.clouway.oauth2.TokenBuilder.aNewToken;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class GetTokenInfoTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private Tokens tokens = context.mock(Tokens.class);
  private TokenInfoController tokenInfoController = new TokenInfoController(tokens);

  @Test
  public void availableToken() throws Exception {
    final DateTime anyTime = new DateTime();
    final Token anyToken = aNewToken().timeToLiveInSeconds(200).createdOn(anyTime).build();

    context.checking(new Expectations() {{
      oneOf(tokens).getNotExpiredToken(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyToken)));
    }});

    Response response = tokenInfoController.handleAsOf(new ParamRequest(Collections.singletonMap("access_token", "::check token id::")), anyTime);
    JsonObject o = new RsPrint(response).asJson();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_OK));
    assertThat(o.get("sub").getAsString(), equalTo(anyToken.identityId));
    assertThat(o.get("exp").getAsString(), equalTo("" + anyTime.plusSeconds(200).asDate().getTime()));
    assertThat(o.get("expires_in").getAsInt(), equalTo(anyToken.expiresInSeconds.intValue()));

  }

  @Test
  public void tokenWasExpired() throws Exception {
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).getNotExpiredToken(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.absent()));
    }});

    Response response = tokenInfoController.handleAsOf(new ParamRequest(Collections.singletonMap("access_token", "::check token id::")), anyTime);

    JsonObject o = new RsPrint(response).asJson();
    assertThat(response.status(), is(equalTo(Status.badRequest())));
    assertThat(o.get("error").getAsString(), is(equalTo("invalid_request")));
  }

  @Test
  public void tokenWasPassedFromRequest() throws Exception {
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).getNotExpiredToken("::some token id::", anyTime);
      will(returnValue(Optional.absent()));
    }});
    tokenInfoController.handleAsOf(new ParamRequest(Collections.singletonMap("access_token", "::some token id::")), anyTime);
  }

}
