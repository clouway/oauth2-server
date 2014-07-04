package com.example.auth.http;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class RegistrationResponseDTOEqualityTest {
  @Test
  public void areEqual() {
    RegistrationResponseDTO response1 = new RegistrationResponseDTO("clientId", "secret");
    RegistrationResponseDTO response2 = new RegistrationResponseDTO("clientId", "secret");

    assertThat(response1, is(response2));
  }

  @Test
  public void areNotEqual() {
    RegistrationResponseDTO response1 = new RegistrationResponseDTO("id1", "secret1");
    RegistrationResponseDTO response2 = new RegistrationResponseDTO("id2", "secret2");

    assertThat(response1, is(not(response2)));
  }
}