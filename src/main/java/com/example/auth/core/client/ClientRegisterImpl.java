package com.example.auth.core.client;

import com.example.auth.core.RegistrationRequest;
import com.example.auth.core.RegistrationResponse;
import com.example.auth.core.token.TokenGenerator;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class ClientRegisterImpl implements ClientRegister {

  private Provider<TokenGenerator> tokenGenerator;
  private ClientRepository repository;

  @Inject
  public ClientRegisterImpl(Provider<TokenGenerator> tokenGenerator, ClientRepository repository) {
    this.tokenGenerator = tokenGenerator;
    this.repository = repository;
  }

  @Override
  public RegistrationResponse register(RegistrationRequest request) {
    TokenGenerator generator = tokenGenerator.get();
    String id = generator.generate();
    String secret = generator.generate();

    Client client = new Client(id, secret, request.name, request.url, request.description, request.redirectURI);

    repository.save(client);

    return new RegistrationResponse(id, secret);
  }

}
