package com.example.auth.http;

import com.example.auth.core.ClientRegister;
import com.example.auth.core.RegistrationRequest;
import com.example.auth.core.RegistrationResponse;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
@At("/register")
public class RegistrationEndpoint {
  private final ClientRegister repository;

  @Inject
  public RegistrationEndpoint(ClientRegister repository) {
    this.repository = repository;
  }

  @Post
  public Reply<RegistrationResponseDTO> register(Request request) {
    RegistrationRequestDTO requestDTO = request.read(RegistrationRequestDTO.class).as(Json.class);
    RegistrationRequest requestDomain = adapt(requestDTO);

    RegistrationResponse registrationResponse = repository.register(requestDomain);

    return Reply.with(adapt(registrationResponse)).as(Json.class);
  }

  private RegistrationRequest adapt(RegistrationRequestDTO dto) {
    return new RegistrationRequest(dto.getName(), dto.getUrl(), dto.getDescription(), dto.getRedirectURI());
  }

  private RegistrationResponseDTO adapt(RegistrationResponse domain) {
    return new RegistrationResponseDTO(domain.clientId, domain.clientSecret);
  }
}