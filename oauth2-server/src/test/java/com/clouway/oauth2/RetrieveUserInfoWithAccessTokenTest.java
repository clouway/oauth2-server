package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.FindIdentityResult;
import com.clouway.oauth2.token.FindIdentityResult.NotFound;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.IdentityFinder;
import com.clouway.oauth2.token.Subject;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;

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
      oneOf(tokens).findTokenAvailableAt("::any token id::", anyInstantTime);
      will(returnValue(Optional.of(new BearerToken("", GrantType.AUTHORIZATION_CODE, new Subject.User("::identity_id::"), "::clientId::", "::user email::", Collections.<String>emptySet(), anyInstantTime, ImmutableMap.of("::index::", "::1::")))));

      oneOf(identityFinder).findIdentity(new FindIdentityRequest(
              new Subject.User("::identity_id::"), anyInstantTime, "::clientId::", Collections.emptyMap()));
      will(returnValue(new FindIdentityResult.User(new Identity("985", "::user name::", "::user given name::", "::family name::", "::user email::", "::user picture::", Collections.<String, Object>emptyMap()))));
    }});

    Response response = new UserInfoController(identityFinder, tokens).handleAsOf(request, anyInstantTime);

    JsonObject result = new RsPrint(response).asJson();
    assertThat(result.get("id").getAsLong(), is(equalTo(985L)));
    assertThat(result.get("email").getAsString(), is(equalTo("::user email::")));
    assertThat(result.get("given_name").getAsString(), is(equalTo("::user given name::")));
    assertThat(result.get("family_name").getAsString(), is(equalTo("::family name::")));
  }

  @Test
  public void identityWithPrivateClaims() throws Exception {
    final Tokens tokens = context.mock(Tokens.class);
    final Request request = new ParamRequest(
            ImmutableMap.of("access_token", "::any token id::", "::otherparam::", "::1::")
    );
    final IdentityFinder identityFinder = context.mock(IdentityFinder.class);
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(new BearerToken("", GrantType.AUTHORIZATION_CODE, new Subject.User("::identity_id::"), "::clientId::", "::user email::", Collections.<String>emptySet(), anyInstantTime, ImmutableMap.of("::index::", "::1::")))));

      oneOf(identityFinder).findIdentity(new FindIdentityRequest(new Subject.User("::identity_id::"), anyInstantTime, "::clientId::", Collections.emptyMap()));
      will(returnValue(new FindIdentityResult.User(new Identity("985", "::user name::", "::user given name::", "::family name::", "::user email::", "::user picture::",
              ImmutableMap.<String, Object>of("claim1", "::any string value::", "claim2", 342)))));
    }});

    Response response = new UserInfoController(identityFinder, tokens).handleAsOf(request, anyInstantTime);
    JsonObject result = new RsPrint(response).asJson();

    assertThat(result.get("claim1").getAsString(), is(equalTo("::any string value::")));
    assertThat(result.get("claim2").getAsInt(), is(equalTo(342)));
  }

  @Test
  public void tokenIsExpired() {
    final Tokens tokens = context.mock(Tokens.class);
    final IdentityFinder identityFinder = context.mock(IdentityFinder.class);

    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt("::expired token id::", anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    Response response = new UserInfoController(identityFinder, tokens).handleAsOf(new ParamRequest(ImmutableMap.of("access_token", "::expired token id::")), anyInstantTime);
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
      oneOf(tokens).findTokenAvailableAt("::any token id::", anyInstantTime);
      will(returnValue(Optional.of(new BearerToken("", GrantType.JWT, new Subject.ServiceAccount("::identity_id::"), "::clientId::", "::user email::", Collections.<String>emptySet(), anyInstantTime, ImmutableMap.of("::index::", "::1::")))));

      oneOf(identityFinder).findIdentity(new FindIdentityRequest(new Subject.ServiceAccount("::identity_id::"), anyInstantTime, "::clientId::", Collections.emptyMap()));
      will(returnValue(NotFound.INSTANCE));
    }});

    Response response = new UserInfoController(identityFinder, tokens).handleAsOf(request, anyInstantTime);
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
  }

}
