package com.clouway.oauth2.token;

import com.clouway.oauth2.util.PemKeyGenerator;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.keystore.IdentityKeyPair;
import com.clouway.oauth2.keystore.KeyStore;
import com.google.common.base.Optional;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Collections;

import static com.clouway.oauth2.token.IdentityBuilder.aNewIdentity;
import static com.clouway.oauth2.common.CalendarUtil.newDateTime;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class JjwtIdTokenFactoryTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Test
  public void createNewIdToken() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);
    final DateTime instant = new DateTime().plusSeconds(60);
    final KeyPair keyPair = PemKeyGenerator.generatePair();

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.singletonList(new IdentityKeyPair("::any key::", keyPair.getPrivate(), keyPair.getPublic()))));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);
    Optional<String> possibleIdToken = factory.create(
            "::any host::", "::any client::",
            aNewIdentity().withId("123").build(), 10L, instant
    );

    Jws<Claims> jwt = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(possibleIdToken.get());

    assertThat(jwt.getBody().getAudience(), is(equalTo("::any client::")));
    assertThat(jwt.getBody().getIssuer(), is(equalTo("::any host::")));
    assertThat(jwt.getBody().getSubject(), is(equalTo("123")));
  }

  @Test
  public void idTokensAreDifferentForEachClient() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);
    final DateTime anyInstantTime = newDateTime(2017, 5, 14, 11, 0, 0);
    final KeyPair firstKeyPair = PemKeyGenerator.generatePair();
    final KeyPair secondKeyPair = PemKeyGenerator.generatePair();

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.singletonList(
              new IdentityKeyPair("::first key::", firstKeyPair.getPrivate(), firstKeyPair.getPublic()))));

      oneOf(keyStore).getKeys();
      will(returnValue(Collections.singletonList(
              new IdentityKeyPair("::second key::", secondKeyPair.getPrivate(), secondKeyPair.getPublic()))));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);

    Optional<String> firstIdToken = factory.create(
            "::any host::", "::client 1::",
            aNewIdentity().withId("123").build(), 10L, anyInstantTime
    );

    Optional<String> secondIdToken = factory.create(
            "::any host::", "::client 2::",
            aNewIdentity().withId("123").build(), 10L, anyInstantTime
    );

    assertThat(firstIdToken.get(), is(not(equalTo(secondIdToken.get()))));
  }

  @Test
  public void noCertificatesAreAvailableForSigningOfKey() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.emptyList()));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);
    Optional<String> possibleIdToken = factory.create("::any host::", "::any client::", aNewIdentity().withId("123").build(), 10L, new DateTime());

    assertThat(possibleIdToken.isPresent(), is(false));
  }

}