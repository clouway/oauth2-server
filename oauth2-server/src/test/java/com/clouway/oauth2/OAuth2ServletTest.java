package com.clouway.oauth2;

import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.token.TokenRepository;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class OAuth2ServletTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  OAuth2Servlet controller;

  @Mock
  HttpServletResponse response;

  @Before
  public void createController() {
    controller = new OAuth2Servlet() {

      @Override
      protected ClientAuthorizationRepository clientAuthorizationRepository() {
        return null;
      }

      @Override
      protected ClientRepository clientRepository() {
        return null;
      }

      @Override
      protected TokenRepository tokenRepository() {
        return null;
      }
    };
  }

  @Test
  public void getIsNotSupported() throws Exception {
    final StringWriter writer = new StringWriter();

    context.checking(new Expectations() {{
      oneOf(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
      oneOf(response).getWriter();
      will(returnValue(new PrintWriter(writer)));
    }});

    controller.doGet(null, response);

    assertThat(writer.toString(), is(equalTo("GET operation is not supported.")));
  }

}