package com.clouway.oauth2.exampleapp;

import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;
import com.google.sitebricks.headless.Request;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
* @author Miroslav Genov (mgenov@gmail.com)
*/
class ParameterRequest implements Request {
  private Map<String, String> parameters;


  static ParameterRequest makeRequestWithParameters(Map<String, String> parameters) {
    return new ParameterRequest(parameters);
  }


  ParameterRequest(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  @Override
  public <T> RequestRead<T> read(Class<T> eClass) {
    return null;
  }

  @Override
  public <E> RequestRead<E> read(TypeLiteral<E> typeLiteral) {
    return null;
  }

  @Override
  public void readTo(OutputStream outputStream) throws IOException {
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
  public String matrixParam(String s) {
    return null;
  }

  @Override
  public String param(String s) {
    return parameters.get(s);
  }

  @Override
  public String header(String s) {
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
}
