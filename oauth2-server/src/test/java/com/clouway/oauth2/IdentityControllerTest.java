package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.Status;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
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
public class IdentityControllerTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  ResourceOwnerIdentityFinder identityFinder;

  @Mock
  IdentityActivity identityActivity;

  @Test
  public void happyPath() throws IOException {
    IdentityController identityController = new IdentityController(identityFinder, identityActivity, "");

    final Request request = new ParamRequest(ImmutableMap.of(
            "client_id", "::client_id::"
    ));
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(identityFinder).find(request, anyInstantTime);
      will(returnValue(Optional.of("::identity_id::")));

      oneOf(identityActivity).execute("::identity_id::", request, anyInstantTime);
      will(returnValue(new RsText("test response")));
    }});

    Response response = identityController.handleAsOf(request, anyInstantTime);
    assertThat(new RsPrint(response).printBody(), is(equalTo("test response")));
  }

  @Test
  public void userWasNotAuthorized() throws IOException {
    IdentityController identityController = new IdentityController(identityFinder, identityActivity, "/r/oauth/login?continue=");
    final Request request = new ParamRequest(ImmutableMap.of("client_id", "::client1::"));
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(identityFinder).find(request, anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    Response response = identityController.handleAsOf(request, anyInstantTime);
    Status status = response.status();

    assertThat(status.code, is(equalTo(HttpURLConnection.HTTP_MOVED_TEMP)));
    assertThat(status.redirectUrl, is(equalTo("/r/oauth/login?continue=%2F%3Fclient_id%3D%3A%3Aclient1%3A%3A")));
  }

}