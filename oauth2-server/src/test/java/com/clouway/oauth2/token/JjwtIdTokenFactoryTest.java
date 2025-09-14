package com.clouway.oauth2.token;

import com.clouway.oauth2.util.PemKeyGenerator;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.keystore.IdentityKeyPair;
import com.clouway.oauth2.keystore.KeyStore;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

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
    String charSequence = factory
            .newBuilder()
            .issuer("::any host::")
            .audience("::any client::")
            .subjectUser(aNewIdentity().withId("123").build())
            .ttl(10L)
            .issuedAt(instant)
            .build();
    
    Jws<Claims> jwt = Jwts.parser().verifyWith(keyPair.getPublic()).build().parseSignedClaims(charSequence);

    assertThat(jwt.getBody().getAudience(), is(equalTo(Collections.singleton("::any client::"))));
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

    String firstIdToken = factory.newBuilder()
            .issuer("::any host::").audience("::client 1::")
            .subjectUser(aNewIdentity().withId("123").build())
            .ttl(10L).issuedAt(anyInstantTime).build();

    String secondIdToken = factory.newBuilder()
            .issuer("::any host::").audience("::client 2::")
            .subjectUser(aNewIdentity().withId("123").build())
            .ttl(10L).issuedAt(anyInstantTime).build();

    assertThat(firstIdToken, is(not(equalTo(secondIdToken))));
  }

  @Test
  public void noCertificatesAreAvailableForSigningOfKey() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.emptyList()));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);
    try {
      factory.newBuilder()
              .issuer("::any host::")
              .audience("::any client::")
              .subjectUser(aNewIdentity().withId("123").build())
              .ttl(10L)
              .issuedAt(new DateTime())
              .build();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage(), is("No signing keys are configured"));
      return;
    }
    throw new AssertionError("Expected IllegalStateException to be thrown");
  }

  @Test
  public void missingIssuedAtThrows() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);
    final KeyPair keyPair = PemKeyGenerator.generatePair();

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.singletonList(new IdentityKeyPair("::kid::", keyPair.getPrivate(), keyPair.getPublic()))));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);
    try {
      factory.newBuilder()
          .issuer("::host::")
          .audience("::client::")
          .subjectUser(aNewIdentity().withId("u1").build())
          .ttl(60L)
          // missing issuedAt
          .build();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage(), is("issuedAt must be specified"));
      return;
    }
    throw new AssertionError("Expected IllegalStateException to be thrown");
  }

  @Test
  public void missingTtlThrows() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);
    final KeyPair keyPair = PemKeyGenerator.generatePair();

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.singletonList(new IdentityKeyPair("::kid::", keyPair.getPrivate(), keyPair.getPublic()))));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);
    try {
      factory.newBuilder()
          .issuer("::host::")
          .audience("::client::")
          .subjectUser(aNewIdentity().withId("u1").build())
          .issuedAt(new DateTime())
          // missing ttl
          .build();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage(), is("ttl must be specified"));
      return;
    }
    throw new AssertionError("Expected IllegalStateException to be thrown");
  }

  @Test
  public void missingSubjectThrows() throws Exception {
    final KeyStore keyStore = context.mock(KeyStore.class);
    final KeyPair keyPair = PemKeyGenerator.generatePair();

    context.checking(new Expectations() {{
      oneOf(keyStore).getKeys();
      will(returnValue(Collections.singletonList(new IdentityKeyPair("::kid::", keyPair.getPrivate(), keyPair.getPublic()))));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(keyStore);
    try {
      factory.newBuilder()
          .issuer("::host::")
          .audience("::client::")
          // missing subject
          .ttl(60L)
          .issuedAt(new DateTime())
          .build();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage(), is("subject must be specified"));
      return;
    }
    throw new AssertionError("Expected IllegalStateException to be thrown");
  }

}
