package com.clouway.oauth2.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Objects;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class CommonMatchers {

  public static <T> Matcher<DateTime> timeIsAt(final int hour, final int minute, final int second) {
    return new TypeSafeMatcher<DateTime>() {
      @Override
      protected boolean matchesSafely(DateTime time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time.asDate());

        return calendar.get(Calendar.HOUR_OF_DAY) == hour
                && calendar.get(Calendar.MINUTE) == minute
                && calendar.get(Calendar.SECOND) == second;
      }

      @Override
      public void describeTo(Description description) {

      }
    };
  }

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
