package com.clouway.oauth2;

import com.clouway.oauth2.authorization.AuthorizationRequest;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.codechallenge.CodeChallenge;
import com.google.common.collect.Sets;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationRequestEqualityTest {
  @Test
  public void areEqual() {
    AuthorizationRequest authorizationRequest1 = new AuthorizationRequest(new Client("::id::","::secret::","::description::", Sets.<String>newHashSet("redirectUrl1","redirectUrl2"),false), "::identityId::", "::responseType::",Sets.<String>newHashSet("scope1","scope2"),new CodeChallenge("::codeChallenge::","::method::"));
    AuthorizationRequest authorizationRequest2 = new AuthorizationRequest(new Client("::id::","::secret::","::description::", Sets.<String>newHashSet("redirectUrl1","redirectUrl2"),false), "::identityId::", "::responseType::",Sets.<String>newHashSet("scope1","scope2"),new CodeChallenge("::codeChallenge::","::method::"));

    assertThat(authorizationRequest1, is(authorizationRequest2));
  }

  @Test
  public void areNotEqual() {
    AuthorizationRequest authorizationRequest1 = new AuthorizationRequest(new Client("::id::","::secret::","::description::", Sets.<String>newHashSet("redirectUrl1","redirectUrl2"),false), "::identityId::", "::responseType::",Sets.<String>newHashSet("scope1","scope2"),new CodeChallenge("::codeChallenge::","::method::"));
    AuthorizationRequest authorizationRequest2 = new AuthorizationRequest(new Client("::di::","::secret::","::description::", Sets.<String>newHashSet("redirectUrl1","redirectUrl2"),false), "::identityId::", "::responseType::",Sets.<String>newHashSet("scope1","scope2"),new CodeChallenge("::codeChallenge::","::method::"));

    assertThat(authorizationRequest1, is(not(authorizationRequest2)));
  }
}