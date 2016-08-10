package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenType;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RetrieveUserInfoWithAccessTokenTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Test
  public void happyPath() throws IOException {
    final Tokens tokens = context.mock(Tokens.class);
    final Request request = new ParamRequest(
            ImmutableMap.of("access_token", "::any token id::")
    );
    final IdentityFinder identityFinder = context.mock(IdentityFinder.class);

    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).getNotExpiredToken("::any token id::", anyInstantTime);
      will(returnValue(Optional.of(new Token("", TokenType.BEARER, "", "::identity_id::", 10L, anyInstantTime))));

      oneOf(identityFinder).findIdentity("::identity_id::", anyInstantTime);
      will(returnValue(Optional.of(new Identity("985", "::user name::", "::user given name::", "::family name::", "::user email::", "::user picture::"))));
    }});

    Response response = new UserInfoController(identityFinder, tokens).handleAsOf(request, anyInstantTime);

    JsonObject result = new RsPrint(response).asJson();
    assertThat(result.get("id").getAsLong(), is(equalTo(985L)));
    assertThat(result.get("email").getAsString(), is(equalTo("::user email::")));
    assertThat(result.get("given_name").getAsString(), is(equalTo("::user given name::")));
    assertThat(result.get("family_name").getAsString(), is(equalTo("::family name::")));
  }

  @Test
  public void tokenIsExpired() {
    final Tokens tokens = context.mock(Tokens.class);

    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).getNotExpiredToken("::expired token id::", anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    Response response = new UserInfoController(null, tokens).handleAsOf(new ParamRequest(ImmutableMap.of("access_token", "::expired token id::")), anyInstantTime);
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
  }

  @Test
  public void identityCannotBeRetrieved() throws IOException {
    final Tokens tokens = context.mock(Tokens.class);
    final Request request = new ParamRequest(
            ImmutableMap.of("access_token", "::any token id::")
    );
    final IdentityFinder identityFinder = context.mock(IdentityFinder.class);

    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).getNotExpiredToken("::any token id::", anyInstantTime);
      will(returnValue(Optional.of(new Token("", TokenType.BEARER, "", "::identity_id::", 10L, anyInstantTime))));

      oneOf(identityFinder).findIdentity("::identity_id::", anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    Response response = new UserInfoController(identityFinder, tokens).handleAsOf(request, anyInstantTime);
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
  }

}
