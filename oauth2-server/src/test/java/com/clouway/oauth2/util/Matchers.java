package com.clouway.oauth2.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class Matchers {

  public static <T> Matcher<T> matching(final T value) {
    return new TypeSafeMatcher<T>() {
      @Override
      protected boolean matchesSafely(T t) {
        return Objects.deepEquals(value, t);
      }

      @Override
      public void describeTo(Description description) {
      }
    };
  }
}
