package com.clouway.oauth2.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Used for capturing the arguments of a invoked method of a mocked object using jmock.
 *
 *
 * final ArgumentCaptor<String> captor = new ArgumentCaptor<String>();
 * context.checking(new Expectations() {{
 *     oneOf(mock).addInvoicePaymentChargedEventHandler(with(captor));
 *    }});
 *
 *  ...some method invocation of a tested class...
 *
 *    assertThat(captor.getValue(), is(equalTo("expected string value")));
 *
 *
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class ArgumentCaptor<T> extends BaseMatcher<T> {

  private T instance;

  public T getValue() {
    return instance;
  }

  public boolean matches(Object o) {
    try {
      instance = (T) o;
      return true;
    } catch (ClassCastException ex) {
      return false;
    }
  }

  public void describeTo(Description description) {
  }
}