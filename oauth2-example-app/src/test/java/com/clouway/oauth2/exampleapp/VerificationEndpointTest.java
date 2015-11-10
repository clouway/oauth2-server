package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.token.TokenVerifier;
import com.google.sitebricks.headless.Reply;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.clouway.oauth2.exampleapp.ReplyMatchers.isBadRequest;
import static com.clouway.oauth2.exampleapp.ReplyMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class VerificationEndpointTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenVerifier verifier = context.mock(TokenVerifier.class);

  private VerificationEndpoint endpoint = new VerificationEndpoint(verifier);

  @Test
  public void existingToken() throws Exception {
    context.checking(new Expectations() {{
      oneOf(verifier).verify("b93568bbf73436511032843ec459f08b");
      will(Expectations.returnValue(true));
    }});

    Reply<?> reply = endpoint.verify("b93568bbf73436511032843ec459f08b");

    assertThat(reply, isOk());
  }

  @Test
  public void notExistingToken() throws Exception {
    context.checking(new Expectations() {{
      oneOf(verifier).verify("2f5a7bba8d0c5e713f059eefd0de9662");
      will(Expectations.returnValue(false));
    }});

    Reply<?> reply = endpoint.verify("2f5a7bba8d0c5e713f059eefd0de9662");

    assertThat(reply, isBadRequest());
  }
}