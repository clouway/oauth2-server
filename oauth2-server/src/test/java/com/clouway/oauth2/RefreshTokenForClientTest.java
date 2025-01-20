package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.FindIdentityResult;
import com.clouway.oauth2.token.FindIdentityResult.NotFound;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.IdTokenFactory;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.IdentityFinder;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static com.clouway.oauth2.token.BearerTokenBuilder.aNewToken;
import static com.clouway.oauth2.token.IdentityBuilder.aNewIdentity;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RefreshTokenForClientTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  Request request;

  @Mock
  Tokens tokens;

  @Mock
  IdentityFinder identityFinder;

  @Mock
  IdTokenFactory idTokenFactory;

  @Test
  public void happyPath() throws IOException {
    RefreshTokenActivity action = new RefreshTokenActivity(tokens, idTokenFactory, identityFinder);
    final Client client = aNewClient().withId("client1").withSecret("secret1").build();
    final Identity identity = aNewIdentity().withId("::identityId::").build();
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(request).param("refresh_token");
      will(returnValue("::refresh_token::"));

      oneOf(tokens).refreshToken("::refresh_token::", anyTime);
      will(returnValue(
              new TokenResponse(true,
                      aNewToken()
                              .withValue("::access_token::")
                              .identityId("::identityId::")
                              .grantType(GrantType.AUTHORIZATION_CODE)
                              .forClient("::clientId::")
                              .expiresAt(anyTime.plusSeconds(600))
                              .build(),
                      "::refresh_token::")
      ));

      oneOf(identityFinder).findIdentity(new FindIdentityRequest("::identityId::", GrantType.AUTHORIZATION_CODE, anyTime, Collections.<String, String>emptyMap(), "::clientId::"));
      will(returnValue(new FindIdentityResult.User(identity)));

      oneOf(request).header("Host");
      will(returnValue("::host::"));

      oneOf(idTokenFactory).create("::host::", client.id, identity, 600L, anyTime);
      will(returnValue(Optional.of("::id_token::")));
    }});

    Response response = action.execute(client, request, anyTime);

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::access_token::"));
    assertThat(body, containsString("600"));
    assertThat(body, containsString("::refresh_token::"));
    assertThat(body, containsString("::id_token::"));
  }

  @Test
  public void idTokenWasNotGenerated() throws Exception {
    RefreshTokenActivity action = new RefreshTokenActivity(tokens, idTokenFactory, identityFinder);
    final Client client = aNewClient().withId("client1").withSecret("secret1").build();
    final Identity identity = aNewIdentity().withId("::identityId::").build();
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(request).param("refresh_token");
      will(returnValue("::refresh_token::"));

      oneOf(tokens).refreshToken("::refresh_token::", anyTime);
      will(returnValue(
              new TokenResponse(true,
                      aNewToken()
                              .withValue("::access_token::")
                              .identityId("::identityId::")
                              .forClient("::clientId::")
                              .grantType(GrantType.AUTHORIZATION_CODE)
                              .expiresAt(anyTime.plusSeconds(600))
                              .build(),
                      "::refresh_token::")
      ));

      oneOf(identityFinder).findIdentity(new FindIdentityRequest("::identityId::", GrantType.AUTHORIZATION_CODE, anyTime, Collections.<String, String>emptyMap(), "::clientId::"));
      will(returnValue(new FindIdentityResult.User(identity)));

      oneOf(request).header("Host");
      will(returnValue("::host::"));

      oneOf(idTokenFactory).create("::host::", client.id, identity, 600L, anyTime);
      will(returnValue(Optional.absent()));
    }});

    Response response = action.execute(client, request, anyTime);

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::access_token::"));
    assertThat(body, containsString("600"));
    assertThat(body, containsString("::refresh_token::"));
    assertFalse(body.contains("id_token"));
  }

  @Test
  public void identityNotFound() throws Exception {
    RefreshTokenActivity action = new RefreshTokenActivity(tokens, idTokenFactory, identityFinder);
    final Client client = aNewClient().withId("client1").withSecret("secret1").build();
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(request).param("refresh_token");
      will(returnValue("::refresh_token::"));

      oneOf(tokens).refreshToken("::refresh_token::", anyTime);
      will(returnValue(
              new TokenResponse(true,
                      aNewToken()
                              .withValue("::access_token::")
                              .identityId("::identityId::")
                              .forClient("::clientId::")
                              .grantType(GrantType.AUTHORIZATION_CODE)
                              .expiresAt(anyTime.plusSeconds(600))
                              .build(),
                      "::refresh_token::")
      ));

      oneOf(identityFinder).findIdentity(new FindIdentityRequest("::identityId::", GrantType.AUTHORIZATION_CODE, anyTime, Collections.<String, String>emptyMap(), "::clientId::"));
      will(returnValue(NotFound.INSTANCE));
    }});

    Response response = action.execute(client, request, anyTime);

    String body = new RsPrint(response).printBody();
    assertThat(body, containsString("identity was not found"));
  }

  @Test
  public void refreshTokenWasExpired() throws IOException {
    RefreshTokenActivity action = new RefreshTokenActivity(tokens, idTokenFactory, identityFinder);
    Client client = aNewClient().withId("client1").withSecret("secret1").build();
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(tokens).refreshToken("::refresh_token::", anyTime);
      will(returnValue(new TokenResponse(false, null, "")));
    }});

    Response response = action.execute(client, new ParamRequest(ImmutableMap.of("refresh_token", "::refresh_token::")), anyTime);

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("invalid_grant"));
  }
}