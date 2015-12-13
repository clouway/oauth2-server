package com.clouway.oauth2.http;

import com.clouway.oauth2.ByteRequest;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class FkRegexTest {

  @Test
  public void matchingPath() throws IOException {
    Optional<Response> opt = new FkRegex("/abc/aaa", new Take() {
      @Override
      public Response ack(Request request) {
        return new RsText("test message");
      }
    }).route(new ByteRequest("/abc/aaa", ImmutableMap.<String, String>of(), "test".getBytes()));
    assertThat(opt.isPresent(), is(equalTo(true)));
  }

  @Test
  public void partialMatching() throws IOException {
    Optional<Response> opt = new FkRegex(".*/aaa", new Take() {
      @Override
      public Response ack(Request request) {
        return new RsText("test message");
      }
    }).route(new ByteRequest("/abc/aaa", ImmutableMap.<String, String>of(), "test".getBytes()));
    assertThat(opt.isPresent(), is(equalTo(true)));
  }

  @Test
  public void notMatchingPath() throws IOException {
    Optional<Response> opt = new FkRegex("/abc/aaa", new Take() {
      @Override
      public Response ack(Request request) {
        return new RsText("test message");
      }
    }).route(new ByteRequest("/aaa/abc", ImmutableMap.<String, String>of(), "test".getBytes()));
    assertThat(opt.isPresent(), is(equalTo(false)));
  }

}