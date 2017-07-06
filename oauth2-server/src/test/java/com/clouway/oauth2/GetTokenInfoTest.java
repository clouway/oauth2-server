package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.Status;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.IdTokenFactory;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.Collections;

import static com.clouway.oauth2.BearerTokenBuilder.aNewToken;
import static com.clouway.oauth2.IdentityBuilder.aNewIdentity;
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

  private IdentityFinder identityFinder = context.mock(IdentityFinder.class);

  private IdTokenFactory idTokenFactory = context.mock(IdTokenFactory.class);

  private Request request = context.mock(Request.class);

  private TokenInfoController tokenInfoController = new TokenInfoController(tokens, identityFinder, idTokenFactory);

  @Test
  public void availableToken() throws Exception {
    final DateTime anyTime = new DateTime();
    final BearerToken anyToken = aNewToken().withValue("::identity id::").forClient("::client id::").params(null)
            .withEmail("email@mail.com").expiresAt(anyTime.plusSeconds(200)).build();
    final Identity anIdentity = aNewIdentity().build();

    context.checking(new Expectations() {{
      oneOf(request).param("access_token");
      will(returnValue("::access token::"));
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyToken)));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)), with(Maps.<String, String>newHashMap()));
      will(returnValue(Optional.of(anIdentity)));
      oneOf(request).header("Host");
      will(returnValue("::host::"));
      oneOf(idTokenFactory).create(with(any(String.class)), with(any(String.class)), with(any(Identity.class)), with(any(Long.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of("::base64.encoded.idToken::")));
    }});

    Response response = tokenInfoController.handleAsOf(request, anyTime);
    JsonObject o = new RsPrint(response).asJson();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_OK));
    assertThat(o.get("azp").getAsString(), equalTo(anyToken.clientId));
    assertThat(o.get("aud").getAsString(), equalTo(anyToken.clientId));
    assertThat(o.get("sub").getAsString(), equalTo(anyToken.identityId));
    assertThat(o.get("exp").getAsString(), equalTo("" + anyTime.plusSeconds(200).asDate().getTime()));
    assertThat(o.get("expires_in").getAsInt(), equalTo(anyToken.ttlSeconds(anyTime).intValue()));
    assertThat(o.get("email").getAsString(), equalTo(anyToken.email));
    assertThat(o.get("id_token").getAsString(), equalTo("::base64.encoded.idToken::"));
  }

  @Test
  public void useTokenParamsWhenLoadIdentity() throws Exception {
    final DateTime anyTime = new DateTime();
    final BearerToken anyToken = aNewToken().withValue("::identity id::").forClient("::client id::").params(ImmutableMap.of("::index::", "::1::"))
            .withEmail("email@mail.com").expiresAt(anyTime.plusSeconds(200)).build();
    final Identity anIdentity = aNewIdentity().build();

    context.checking(new Expectations() {{
      oneOf(request).param("access_token");
      will(returnValue("::access token::"));
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyToken)));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)), with(ImmutableMap.of("::index::", "::1::")));
      will(returnValue(Optional.of(anIdentity)));
      oneOf(request).header("Host");
      will(returnValue("::host::"));
      oneOf(idTokenFactory).create(with(any(String.class)), with(any(String.class)), with(any(Identity.class)), with(any(Long.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of("::base64.encoded.idToken::")));
    }});

    Response response = tokenInfoController.handleAsOf(request, anyTime);
  }

  @Test
  public void idTokenWasNotAvailable() throws Exception {
    final DateTime anyTime = new DateTime();
    final BearerToken anyToken = aNewToken().withValue("::identity id::").forClient("::client id::")
            .withEmail("email@mail.com").expiresAt(anyTime.plusSeconds(200)).params(ImmutableMap.of("::index::", "::1::")).build();
    final Identity anIdentity = aNewIdentity().build();

    context.checking(new Expectations() {{
      oneOf(request).param("access_token");
      will(returnValue("::access token::"));
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyToken)));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)), with(ImmutableMap.of("::index::", "::1::")));
      will(returnValue(Optional.of(anIdentity)));
      oneOf(request).header("Host");
      will(returnValue("::host::"));
      oneOf(idTokenFactory).create(with(any(String.class)), with(any(String.class)), with(any(Identity.class)), with(any(Long.class)), with(any(DateTime.class)));
      will(returnValue(Optional.absent()));
    }});

    Response response = tokenInfoController.handleAsOf(request, anyTime);
    JsonObject o = new RsPrint(response).asJson();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_OK));

    assertThat(o.has("id_token"), is(false));
  }


  @Test
  public void tokenWasExpired() throws Exception {
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
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
      oneOf(tokens).findTokenAvailableAt("::some token id::", anyTime);
      will(returnValue(Optional.absent()));
    }});
    tokenInfoController.handleAsOf(new ParamRequest(Collections.singletonMap("access_token", "::some token id::")), anyTime);
  }

}
