package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRegistrationRequest;

/**
 *  @author Ianislav Nachev <qnislav.nachev@gmail.com>
 */
public interface ClientRegistry {

  Client register(Client client);

  Client register(ClientRegistrationRequest request);
}
