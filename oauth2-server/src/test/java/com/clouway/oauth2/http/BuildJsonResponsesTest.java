package com.clouway.oauth2.http;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BuildJsonResponsesTest {

  @Test
  public void happyPath() throws IOException {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("key1", "value1");
    RsJson json = new RsJson(jsonObject);
    assertThat(new RsPrint(json).print(), is(equalTo("Content-Type: application/json\r\n{\"key1\":\"value1\"}")));
  }

  @Test
  public void responseWithMultipleProperties() throws IOException {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("::key1::", "::value1::");
    jsonObject.addProperty("::key2::", "::value2::");
    RsJson json = new RsJson(jsonObject);
    assertThat(new RsPrint(json).print(), is(equalTo("Content-Type: application/json\r\n{\"::key1::\":\"::value1::\",\"::key2::\":\"::value2::\"}")));
  }

}