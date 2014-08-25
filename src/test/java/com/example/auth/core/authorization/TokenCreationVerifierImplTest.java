package com.example.auth.core.authorization;

import com.example.auth.core.Clock;
import com.example.auth.core.token.refreshtoken.RefreshToken;
import com.example.auth.core.token.refreshtoken.RefreshTokenRepository;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenCreationVerifierImplTest {
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenCreationVerifier verifier;

  private String code = "code";

  private String clientId = "clientId";
  private String secret = "the secret";
  @Mock
  private ClientAuthorizationRepository repository;

  private Date currentDate = new Date();

  private Clock clock = new StubClock(currentDate);
  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Before
  public void setUp() throws Exception {
    verifier = new TokenCreationVerifierImpl(repository, clock, refreshTokenRepository);
  }

  @Test
  public void verify() throws Exception {

    final ClientAuthorizationRequest authorizationRequest = new ClientAuthorizationRequest("type", clientId, "Code", "redirectURI");

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.of(authorizationRequest)));
      oneOf(repository).update(authorizationRequest);
    }});

    assertTrue(verifier.verify(code, clientId));
    assertFalse(authorizationRequest.isNotUsed());
  }

  @Test
  public void notVerifyRequestWhenNotExists() throws Exception {

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.absent()));
    }});

    assertFalse(verifier.verify(code, clientId));
  }

  @Test
  public void notVerifiedWhenOtherClientIdWasPassed() throws Exception {

    final ClientAuthorizationRequest authorizationRequest = new ClientAuthorizationRequest("type", "other_clientId", "Code", "redirectURI");

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.of(authorizationRequest)));
    }});

    assertFalse(verifier.verify(code, clientId));
  }

  @Test
  public void notVerifiedWhenAlreadyUsedAuthorization() throws Exception {

    final ClientAuthorizationRequest authorizationRequest = new ClientAuthorizationRequest("type", "other_clientId", "Code", "redirectURI");
    //already used
    authorizationRequest.usedOn(new Date());

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.of(authorizationRequest)));
    }});

    assertFalse(verifier.verify(code, clientId));
  }

  @Test
  public void validRefreshToken() throws Exception {

    final String refreshToken = "24323 234 rtwerrefresh ";

    final RefreshToken token = new RefreshToken(refreshToken, clientId, secret);

    context.checking(new Expectations() {{
      oneOf(refreshTokenRepository).load(refreshToken);
      will(returnValue(Optional.of(token)));
    }});


    assertTrue(verifier.verifyRefreshToken(clientId, secret, refreshToken));

  }

  @Test
  public void existingTokenForOtherClient() throws Exception {

    final String refreshToken = "24323 234 rtwerrefresh ";

    final RefreshToken token = new RefreshToken(refreshToken, clientId, secret);

    context.checking(new Expectations() {{
      oneOf(refreshTokenRepository).load(refreshToken);
      will(returnValue(Optional.of(token)));
    }});

    assertFalse(verifier.verifyRefreshToken("differentClientId", secret, refreshToken));

  }

  @Test
  public void existingTokenSecretNotMatch() throws Exception {

    final String refreshToken = "24323 234 rtwerrefresh ";

    final RefreshToken token = new RefreshToken(refreshToken, clientId, secret);

    context.checking(new Expectations() {{
      oneOf(refreshTokenRepository).load(refreshToken);
      will(returnValue(Optional.of(token)));
    }});

    assertFalse(verifier.verifyRefreshToken(clientId, "some wrong secret", refreshToken));

  }

  @Test
  public void notExistingRefreshToken() throws Exception {

    final String refreshToken = "24323 234 rtwerrefresh ";

    context.checking(new Expectations() {{
      oneOf(refreshTokenRepository).load(refreshToken);
      will(returnValue(Optional.absent()));
    }});

    assertFalse(verifier.verifyRefreshToken(clientId, "some wrong secret", refreshToken));

  }
}