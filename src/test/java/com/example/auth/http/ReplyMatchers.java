package com.example.auth.http;

import com.google.sitebricks.headless.Reply;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_RESET_CONTENT;
import static org.junit.Assert.fail;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class ReplyMatchers {

  public static Matcher<Reply<?>> containsValue(final Object value) {
    return contains(value);
  }

  public static Matcher<Reply<?>> contains(final Object value) {
    return new TypeSafeMatcher<Reply<?>>() {
      @Override
      public boolean matchesSafely(Reply<?> reply) {
        Object responseValue = property("entity", reply);
        return value.equals(responseValue);
      }

      public void describeTo(Description description) {
        description.appendText("reply value was different from expected one");
      }
    };
  }

  public static Matcher<Reply<?>> isOk() {
    return returnCodeMatcher(SC_OK);
  }

  public static Matcher<Reply<?>> isCreated(){
    return returnCodeMatcher(SC_CREATED);
  }

  public static Matcher<Reply<?>> isAccepted(){
    return returnCodeMatcher(SC_ACCEPTED);
  }

  public static Matcher<Reply<?>> isResetContent() {
    return returnCodeMatcher(SC_RESET_CONTENT);
  }

  public static Matcher<Reply<?>> isBadRequest() {
    return returnCodeMatcher(SC_BAD_REQUEST);
  }
  
  public static Matcher<Reply<?>> isNotFound() {
    return returnCodeMatcher(SC_NOT_FOUND);
  }

  public static Matcher<Reply<?>> isInternalServerError() {
    return returnCodeMatcher(SC_INTERNAL_SERVER_ERROR);
  }

  private static Matcher<Reply<?>> returnCodeMatcher(final int expectedCode) {
    return new TypeSafeMatcher<Reply<?>>() {
      @Override
      public boolean matchesSafely(Reply<?> reply) {
        Integer status = property("status", reply);
        return Integer.valueOf(expectedCode).equals(status);
      }

      public void describeTo(Description description) {
        description.appendText("status of the replay was different from expected");
      }
    };
  }

  private static <T> T property(String fieldName, Reply<?> reply) {
    Field field = null;
    try {
      field = reply.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      T actual = (T) field.get(reply);

      return actual;
    } catch (NoSuchFieldException e) {
      fail(e.getMessage());
    } catch (IllegalAccessException e) {
      fail(e.getMessage());
    } finally {
      if (field != null) {
        field.setAccessible(false);
      }
    }
    return null;
  }
}
