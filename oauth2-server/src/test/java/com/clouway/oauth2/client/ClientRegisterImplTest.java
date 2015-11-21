package com.clouway.oauth2.client;

import com.clouway.oauth2.util.ArgumentCaptor;
import com.clouway.oauth2.token.TokenGenerator;
import com.google.inject.util.Providers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class ClientRegisterImplTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private TokenGenerator tokenGenerator;

  private ClientRegister register;

  @Mock
  private ClientRepository repository;

  @Before
  public void setUp() throws Exception {
    register = new ClientRegisterImpl(tokenGenerator, repository);
  }

  @Test
  public void register() throws Exception {

    final String id = "id";
    final String secret = "secret1123";
    final RegistrationRequest request = new RegistrationRequest("name1", "url1", "description1", "redirectURI1");
    final ArgumentCaptor<Client> clientCaptor = new ArgumentCaptor<Client>();
    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue(id));

      oneOf(tokenGenerator).generate();
      will(returnValue(secret));

      oneOf(repository).save(with(clientCaptor));
    }});

    RegistrationResponse response = register.register(request);

    assertThat(response.clientId, is(equalTo(id)));
    assertThat(response.clientSecret, is(equalTo(secret)));

    Client savedClient = clientCaptor.getValue();
    assertThat(savedClient.description, is(equalTo(request.description)));
    assertThat(savedClient.id, is(equalTo(id)));
    assertThat(savedClient.name, is(equalTo(request.name)));
    assertThat(savedClient.redirectURI, is(equalTo(request.redirectURI)));
    assertThat(savedClient.url, is(equalTo(request.url)));
    assertThat(savedClient.secret, is(equalTo(secret)));
  }
}