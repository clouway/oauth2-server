package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.Duration;

import com.clouway.oauth2.exampleapp.ResourceOwnerStore;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.client.ServiceAccountFinder;
import com.clouway.oauth2.exampleapp.ResourceOwnerAuthentication;
import com.clouway.oauth2.exampleapp.SessionSecurity;
import com.clouway.oauth2.exampleapp.UserLoader;
import com.clouway.oauth2.exampleapp.UserLoaderImpl;
import com.clouway.oauth2.exampleapp.UserRepository;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.token.UrlSafeTokenGenerator;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.user.ResourceOwnerIdentityFinder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Collections;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryModule extends AbstractModule {
  @Override
  protected void configure() {

    InMemoryResourceOwnerRepository resourceOwnerRepository = new InMemoryResourceOwnerRepository(new UrlSafeTokenGenerator());

    bind(ResourceOwnerStore.class).toInstance(resourceOwnerRepository);
    bind(ResourceOwnerAuthentication.class).toInstance(resourceOwnerRepository);
    bind(SessionSecurity.class).toInstance(resourceOwnerRepository);

    InMemoryClientRepository clientRepository = new InMemoryClientRepository();
    clientRepository.register(new Client("fe72722a40de846e03865cb3b582aed57841ac71", "857613db7b18232c72a5093ad19dbc6df74a139e", "testname", "http://localhost:8080", "test", Collections.singleton("http://localhost:8080/oauth/callback")));
    clientRepository.registerServiceAccount("xxx@apps.clouway.com", "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCH/eazwg0BwuFx\n" +
            "PmXOoauqD54ZPN+3XRF8FxrYo0XvQ8TiJAEJBJo/qjNahn4YYl/6RbP8YLHCe3nd\n" +
            "40tf42fwvLDFBSyFjKIOOiEEilhxjVX1jPsE4fHO+gSthmyzGgjR4bPCPLCeocQj\n" +
            "UQTguDsl2NARDWHomeC0eJgkS9cPfBdRGyIsgQWsnON+SbMd7cN9iXbIhU/TguLE\n" +
            "aW6WAg/rGX9iMiSsl5XcW6wOGU24uRPTNIySzncLY+jEQeFmEw1g0oa7+ZKuxDce\n" +
            "jJTzo4V0BcEKNiiZvH4Y7t/qsXiDeY13NemZVMQ/H9BIyfBpPdiTphnZEwf5XlUJ\n" +
            "pKekD/KBAgMBAAECggEAZeFXltAIAovHbZl7mAQSoUM2BF5QlASLdtWwbSBU4l15\n" +
            "AJpMlD74eD3AX09m5Em+8baKksa2Jadvs0X3UA0D75zNKa0on5yuQ85UshwbCmcC\n" +
            "QQWvgQbsq00vd/i/MqaMeQCINTpWb2Ftma+24cvjtATsS/okoae2aj32bSrMIXKK\n" +
            "V9UC2IwN5rzoJPEFQ6fPaWcTnFMOINY1UFDerVNhn7aT18ubB25N3TcQFA+IkLjZ\n" +
            "+RXAVsDqmmdiCA0IxF18SkC22TCLvWMW8pYAYWYi9MLOCwwjUwv1C3w38qvlzfRP\n" +
            "VDObsDgrmYA5xyJU1LIzvnaWYa+br6VmnBdCqBTTSQKBgQDzc+pkjol6qDVJb8ZD\n" +
            "oa2mNZJCu3dNYSuB9fYGutIT8/BuBZ+qkmb54C6Ny7M0HjpxQpSOToOpH95HlznJ\n" +
            "NLY80s76E9MdX6xFIlTpBRJnu3iPizCXRQV5C0mwleARVsk0FjE5T0XLQMp3l1bd\n" +
            "AGMGJhM9fM0W3mx2gmTjU+DRGwKBgQCPACjFI1aFIbFtpOQtFgHtCMdymhzLVabc\n" +
            "SyY7fnMoQiiPJMja9reFiSuJbF/BpsAxaOKEaVjivPM3arVaf3pyrqC3ZEwurfwx\n" +
            "u+mnXhUftT8/1esZZBGhFPA321zIaDucKx7yfOgg+gRdi6r4gpYD0pQo7sco86Ve\n" +
            "ARzhgUWgkwKBgGVsaj8gXsgZ4bFJfrjYV4bCFL/2Z7p1+/E1rhyZokGrxAOiFiWy\n" +
            "vnHlYp+yOGNDIKfkzA0JSrKf0zPSHcHkUvO+A3qN3csD+7oFlohJk6RhptVucHzk\n" +
            "xWXrPPTzS5kNpd8sS6+LhhEqWe8+vnJt4dNC84sPPkYDvf4VTsCiRiv3AoGAGjx1\n" +
            "PnYVUae03eD63CrFf6+0qBoOXmAAlTpUcWXpyuEYf+rHzySk1yMrkbMIfocRi/8q\n" +
            "UBDj9fWkye4SB+CLnq7bXcpRD99r/dP0MnjYd1DRoeyljasGcP9ec2ETzNES3rwq\n" +
            "mWLBVAuK8X7Gh4Gt9FWWSUxFzgWluXGK0vTcyXECgYA7GyyBORukyfOb5mCrIXm5\n" +
            "kYztpvfhglrUZ23vEzXLk+KPmDao0X3K6fv6OuvuI2oVAZ6TzTT1OlmF/elvP7JX\n" +
            "4vlOXSxLBduB1cInuZFylB99qRmGMCBWhpIobXyRQIZWnaQnsGDfFJiBrBgzN55U\n" +
            "UqgbFBNjeedWV+Hm6ftwxw==\n" +
            "-----END PRIVATE KEY-----");

    bind(ServiceAccountFinder.class).toInstance(clientRepository);
    bind(ClientRepository.class).toInstance(clientRepository);

    InMemoryClientAuthorizationRepository authorizationRepository = new InMemoryClientAuthorizationRepository(new UrlSafeTokenGenerator());
    bind(ClientAuthorizationRepository.class).toInstance(authorizationRepository);

    InMemoryUserRepository userRepository = new InMemoryUserRepository();

    bind(IdentityFinder.class).toInstance(userRepository);
    bind(ResourceOwnerIdentityFinder.class).toInstance(userRepository);

    bind(UserRepository.class).toInstance(userRepository);
  }

  @Provides
  @Singleton
  public Tokens getTokenRepository() {
    return new InMemoryTokens(new UrlSafeTokenGenerator(), new Duration(900000000L));
  }

  @Provides
  public UserLoader getUserLoader(UserRepository userRepository, Tokens tokens) {
    return new UserLoaderImpl(userRepository, tokens);
  }

}