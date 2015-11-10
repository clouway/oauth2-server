package com.clouway.oauth2.exampleapp;

import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;
import com.google.sitebricks.client.Transport;
import com.google.sitebricks.headless.Request;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Adelin Ghanayem adelin.ghanaem@clouway.com
 */
public class SiteBricksRequestMockery {

  public static <E> Request mockRequest(final E request) {
    return new Request() {
      private E e = request;

      @Override
      public <E> RequestRead<E> read(Class<E> type) {
        return new RequestRead<E>() {
          @Override
          public E as(Class<? extends Transport> transport) {
            return (E) request;
          }
        };
      }

      @Override
      public <E> RequestRead<E> read(TypeLiteral<E> type) {
        return null;
      }

      @Override
      public void readTo(OutputStream out) throws IOException {
        if (e instanceof String) {
          out.write(((String) e).getBytes());
        } else {
          throw new IllegalStateException("Try to test with some string ! ");
        }
      }

      @Override
      public Multimap<String, String> headers() {
        return null;
      }

      @Override
      public Multimap<String, String> params() {
        return null;
      }

      @Override
      public Multimap<String, String> matrix() {
        return null;
      }

      @Override
      public String matrixParam(String name) {
        return null;
      }

      @Override
      public String param(String name) {
        return null;
      }

      @Override
      public String header(String name) {
        return null;
      }

      @Override
      public String uri() {
        return null;
      }

      @Override
      public String path() {
        return null;
      }

      @Override
      public String context() {
        return null;
      }

      @Override
      public String method() {
        return null;
      }
    };
  }

}
