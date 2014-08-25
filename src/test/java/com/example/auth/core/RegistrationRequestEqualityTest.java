package com.example.auth.core;

import com.example.auth.core.client.RegistrationRequest;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class RegistrationRequestEqualityTest {
  @Test
  public void areEqual() {
    RegistrationRequest request1 = new RegistrationRequest("name", "url", "description", "redirectURI");
    RegistrationRequest request2 = new RegistrationRequest("name", "url", "description", "redirectURI");

    assertThat(request1, is(request2));
  }

  @Test
  public void areNotEqual() {
    RegistrationRequest request1 = new RegistrationRequest("name1", "url1", "description1", "redirectURI1");
    RegistrationRequest request2 = new RegistrationRequest("name2", "url2", "description2", "redirectURI2");

    assertThat(request1, is(not(request2)));
  }
}