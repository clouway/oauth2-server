package com.clouway.oauth2.token;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class UrlSafeTokenGeneratorTest {

  @Test
  public void happyPath() {
    String token = new UrlSafeTokenGenerator().generate();
    assertThat(token,is(not(nullValue())));
  }

  @Test
  public void uniqueTokensAreGenerated() {
    UrlSafeTokenGenerator tokenGenerator = new UrlSafeTokenGenerator();
    Set<String> tokens = Sets.newHashSet();
    for (int i = 0; i < 10;i++) {
      tokens.add(tokenGenerator.generate());
    }
    assertThat(tokens.size(), is(equalTo(10)));
  }
}