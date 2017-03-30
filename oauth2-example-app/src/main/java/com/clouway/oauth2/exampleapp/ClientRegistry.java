package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRegistrationRequest;

/**
 *  Created by IaNiTyy on 30.03.17.
 */
public interface ClientRegistry {

  Client register(Client client);

  Client register(ClientRegistrationRequest request);
}
