package com.clouway.oauth2.user;

import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class UserLoaderImplTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  private final String token = "the otken csxc< csjf ks";
  private final User user = new User("id","test@mail.com", "name");

  private UserLoader userLoader;

  @Mock
  private UserRepository repository;
  @Mock
  private TokenRepository tokenRepository;

  @Before
  public void setUp() throws Exception {
    userLoader = new UserLoaderImpl(repository, tokenRepository);
  }

  @Test
  public void load() throws Exception {

    context.checking(new Expectations() {{
      oneOf(tokenRepository).getNotExpiredToken(token);
      will(returnValue(Optional.of(new Token("v", "type", "refresh", "userId", 0l, new Date()))));
      oneOf(repository).load("userId");
      will(returnValue(Optional.of(user)));
    }});

    Optional<User> actualUser = userLoader.load(token);

    assertThat(actualUser.get(), is(equalTo(user)));
  }

  @Test
  public void noUserFound() throws Exception {

    context.checking(new Expectations() {{
      oneOf(tokenRepository).getNotExpiredToken(token);
      will(returnValue(Optional.of(new Token("v", "type", "refresh", "userId", 0l, new Date()))));
      oneOf(repository).load("userId");
      will(returnValue(Optional.absent()));
    }});

    Optional<User> actualUser = userLoader.load(token);

    assertFalse(actualUser.isPresent());
  }

  @Test
  public void noNotExpiredTokenFound() throws Exception {

    context.checking(new Expectations() {{
      oneOf(tokenRepository).getNotExpiredToken(token);
      will(returnValue(Optional.absent()));
    }});

    Optional<User> actualUser = userLoader.load(token);

    assertFalse(actualUser.isPresent());
  }
}