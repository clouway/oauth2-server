package com.example.auth.http;

import com.example.auth.core.ClientRepository;
import com.example.auth.core.RegistrationRequest;
import com.example.auth.core.RegistrationResponse;
import com.google.sitebricks.headless.Reply;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.example.auth.http.ReplyMatchers.containsValue;
import static com.example.auth.http.SiteBricksRequestMockery.mockRequest;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class RegistrationEndpointTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientRepository repository = context.mock(ClientRepository.class);

  private RegistrationEndpoint endpoint = new RegistrationEndpoint(repository);

  @Test
  public void register() throws Exception {
    final RegistrationRequest registrationRequest = new RegistrationRequest("Ivan", "http://ivan.com/", "no description", "http://ivan.com/success");
    final RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO("Ivan", "http://ivan.com/", "no description", "http://ivan.com/success");

    final RegistrationResponse registrationResponse = new RegistrationResponse("123456789", "987654321");
    final RegistrationResponseDTO registrationResponseDTO = new RegistrationResponseDTO("123456789", "987654321");

    context.checking(new Expectations() {{
      oneOf(repository).register(registrationRequest);
      will(returnValue(registrationResponse));
    }});

    Reply<RegistrationResponseDTO> response = endpoint.register(mockRequest(registrationRequestDTO));
    assertThat(response, containsValue(registrationResponseDTO));
  }
}