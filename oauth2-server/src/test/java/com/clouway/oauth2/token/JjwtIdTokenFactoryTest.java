package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.PemKeyGenerator;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.jws.RsaJwsSignature;
import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import static com.clouway.oauth2.IdentityBuilder.aNewIdentity;
import static com.clouway.oauth2.util.CalendarUtil.newDateTime;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class JjwtIdTokenFactoryTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Test
  public void createNewIdToken() throws Exception {
    final ClientKeyStore clientKeyStore = context.mock(ClientKeyStore.class);
    final DateTime instant = newDateTime(2017, 6, 14, 10, 0, 0);
    final Block privateKey = PemKeyGenerator.generatePrivateKey();

    context.checking(new Expectations() {{
      oneOf(clientKeyStore).privateCertificates();

      will(returnValue(Collections.singletonMap("::any key::", privateKey)));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(clientKeyStore);
    Optional<String> possibleIdToken = factory.create(
            "::any host::", "::any client::",
            aNewIdentity().withId("123").build(), 10L, instant
    );

    Map<String, Object> values = parseIdToken(possibleIdToken.get());
    assertTokenSignature(possibleIdToken.get(), privateKey);
    assertThat(values.containsValue("::any host::"), is(true));
    assertThat(values.containsValue("::any client::"), is(true));
    assertThat(values.containsValue("123"), is(true));
  }

  @Test
  public void idTokensAreDifferentForEachClient() throws Exception {
    final ClientKeyStore clientKeyStore = context.mock(ClientKeyStore.class);
    final DateTime instant = newDateTime(2017, 5, 14, 11, 0, 0);
    final Block privateKey = PemKeyGenerator.generatePrivateKey();

    context.checking(new Expectations() {{
      exactly(2).of(clientKeyStore).privateCertificates();
      will(returnValue(Collections.singletonMap("::any key::", privateKey)));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(clientKeyStore);

    Optional<String> firstIdToken = factory.create(
            "::any host::", "::client 1::",
            aNewIdentity().withId("123").build(), 10L, instant
    );

    Optional<String> secondIdToken = factory.create(
            "::any host::", "::client 2::",
            aNewIdentity().withId("123").build(), 10L, instant
    );

    assertThat(firstIdToken.get(), is(not(equalTo(secondIdToken.get()))));
  }

  @Test
  public void noCertificatesAreAvailableForSigningOfKey() throws Exception {
    final ClientKeyStore clientKeyStore = context.mock(ClientKeyStore.class);

    context.checking(new Expectations() {{
      oneOf(clientKeyStore).privateCertificates();
      will(returnValue(Collections.emptyMap()));
    }});

    JjwtIdTokenFactory factory = new JjwtIdTokenFactory(clientKeyStore);
    Optional<String> possibleIdToken = factory.create("::any host::", "::any client::", aNewIdentity().withId("123").build(), 10L, new DateTime());

    assertThat(possibleIdToken.isPresent(), is(false));
  }

  private Map<String, Object> parseIdToken(String idToken) {
    String parts[] = idToken.split("\\.");
    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();
    return new Gson().fromJson(new InputStreamReader(new ByteArrayInputStream(BaseEncoding.base64Url().decode(parts[1]))), type);
  }

  private void assertTokenSignature(String idToken, Block key) {
    String parts[] = idToken.split("\\.");
    String tokenWithoutSignature = String.format("%s.%s", parts[0], parts[1]);
    RsaJwsSignature rsaJwsSignature = new RsaJwsSignature(BaseEncoding.base64Url().decode(parts[2]));

    boolean tokenIsValid = rsaJwsSignature.verifyWithPrivateKey(tokenWithoutSignature.getBytes(), key);
    assertTrue(tokenIsValid);
  }

}