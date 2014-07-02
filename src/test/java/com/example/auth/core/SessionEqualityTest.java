package com.example.auth.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SessionEqualityTest {
  @Test
  public void areEqual() {
    Session session1 = new Session("session");
    Session session2 = new Session("session");

    assertThat(session1, is(session2));
  }

  @Test
  public void areNotEqual() {
    Session session1 = new Session("session1");
    Session session2 = new Session("session2");

    assertThat(session1, is(not(session2)));
  }
}