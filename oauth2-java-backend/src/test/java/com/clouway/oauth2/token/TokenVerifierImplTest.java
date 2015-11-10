package com.clouway.oauth2.token;

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

public class TokenVerifierImplTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenVerifier verifier;
  @Mock
  private TokenRepository repository;

  private String tokenValue = "9c5084d190264d0de737a8049ed630fd";

  @Before
  public void setUp() throws Exception {
    verifier = new TokenVerifierImpl(repository);
  }

  @Test
  public void happyPath() throws Exception {
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer","refresh", "userId", 1l, new Date());

    context.checking(new Expectations() {{
      oneOf(repository).getNotExpiredToken(tokenValue);
      will(returnValue(Optional.of(token)));
    }});

    Boolean exists = verifier.verify(tokenValue);

    assertTrue(exists);
  }


  @Test
  public void notExistingToken() throws Exception {
    context.checking(new Expectations() {{
      oneOf(repository).getNotExpiredToken("1265fff34bdc4d976b02196845edb967");
      will(returnValue(Optional.absent()));
    }});

    Boolean exists = verifier.verify("1265fff34bdc4d976b02196845edb967");
    assertFalse(exists);
  }

}