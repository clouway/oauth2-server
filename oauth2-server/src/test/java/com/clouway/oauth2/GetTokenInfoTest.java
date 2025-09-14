package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.Status;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.FindIdentityResult;
import com.clouway.oauth2.token.IdTokenFactory;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.IdentityFinder;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.Collections;

import static com.clouway.oauth2.token.BearerTokenBuilder.aNewToken;
import static com.clouway.oauth2.token.IdentityBuilder.aNewIdentity;
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

      oneOf(identityFinder).findIdentity(with(any(FindIdentityRequest.class)));
      will(returnValue(new FindIdentityResult.User(anIdentity)));
      oneOf(request).header("Host");
      will(returnValue("::host::"));
      allowing(request).header("Host"); will(returnValue("::host::"));
      allowing(idTokenFactory).newBuilder(); will(returnValue(new com.clouway.oauth2.token.IdTokenBuilder() {
        @Override public com.clouway.oauth2.token.IdTokenBuilder issuer(String issuer) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder audience(String audience) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder issuedAt(com.clouway.oauth2.common.DateTime instant) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder ttl(Long ttlSeconds) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder subjectUser(com.clouway.oauth2.token.Identity identity) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder subjectServiceAccount(com.clouway.oauth2.token.ServiceAccount serviceAccount) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder withAccessToken(String accessToken) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder claim(String name, Object value) { return this; }
        @Override public String build() { return "::base64.encoded.idToken::"; }
      }));
    }});

    Response response = tokenInfoController.handleAsOf(request, anyTime);
    JsonObject o = new RsPrint(response).asJson();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_OK));
    assertThat(o.get("azp").getAsString(), equalTo(anyToken.clientId));
    assertThat(o.get("aud").getAsString(), equalTo(anyToken.clientId));
    String sub;
    if (anyToken.subject instanceof com.clouway.oauth2.token.Subject.User) {
      sub = ((com.clouway.oauth2.token.Subject.User) anyToken.subject).getId();
    } else {
      sub = ((com.clouway.oauth2.token.Subject.ServiceAccount) anyToken.subject).getId();
    }
    assertThat(o.get("sub").getAsString(), equalTo(sub));
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

      oneOf(identityFinder).findIdentity(with(any(FindIdentityRequest.class)));
      will(returnValue(new FindIdentityResult.User(anIdentity)));
      oneOf(request).header("Host");
      will(returnValue("::host::"));
      allowing(request).header("Host"); will(returnValue("::host::"));
      allowing(idTokenFactory).newBuilder(); will(returnValue(new com.clouway.oauth2.token.IdTokenBuilder() {
        @Override public com.clouway.oauth2.token.IdTokenBuilder issuer(String issuer) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder audience(String audience) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder issuedAt(com.clouway.oauth2.common.DateTime instant) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder ttl(Long ttlSeconds) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder subjectUser(com.clouway.oauth2.token.Identity identity) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder subjectServiceAccount(com.clouway.oauth2.token.ServiceAccount serviceAccount) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder withAccessToken(String accessToken) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder claim(String name, Object value) { return this; }
        @Override public String build() { return "::base64.encoded.idToken::"; }
      }));
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

      oneOf(identityFinder).findIdentity(with(any(FindIdentityRequest.class)));
      will(returnValue(new FindIdentityResult.User(anIdentity)));
      oneOf(request).header("Host");
      will(returnValue("::host::"));
      allowing(request).header("Host"); will(returnValue("::host::"));
      allowing(idTokenFactory).newBuilder(); will(returnValue(new com.clouway.oauth2.token.IdTokenBuilder() {
        @Override public com.clouway.oauth2.token.IdTokenBuilder issuer(String issuer) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder audience(String audience) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder issuedAt(com.clouway.oauth2.common.DateTime instant) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder ttl(Long ttlSeconds) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder subjectUser(com.clouway.oauth2.token.Identity identity) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder subjectServiceAccount(com.clouway.oauth2.token.ServiceAccount serviceAccount) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder withAccessToken(String accessToken) { return this; }
        @Override public com.clouway.oauth2.token.IdTokenBuilder claim(String name, Object value) { return this; }
        @Override public String build() { return "::base64.encoded.idToken::"; }
      }));
    }});

    Response response = tokenInfoController.handleAsOf(request, anyTime);
    JsonObject o = new RsPrint(response).asJson();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_OK));

    assertThat(o.has("id_token"), is(true));
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
