package com.clouway.oauth2.http;

import org.junit.Test;

import javax.json.Json;
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
    RsJson json = new RsJson(Json.createObjectBuilder().add("key1","value1").build());
    assertThat(new RsPrint(json).print(),is(equalTo("Content-Type: application/json\r\n{\"key1\":\"value1\"}")));
  }

}