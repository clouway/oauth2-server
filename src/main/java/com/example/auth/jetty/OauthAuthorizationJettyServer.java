package com.example.auth.jetty;

import com.example.auth.http.OauthAuthorizationServerModule;
import com.example.auth.memory.InMemoryModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class OauthAuthorizationJettyServer {
  private final Server server;

  public OauthAuthorizationJettyServer(Integer port) {
    server = new Server(port);
  }

  public void start() throws Exception {
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");

    context.addServlet(DefaultServlet.class, "/");
    context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

    context.addEventListener(new GuiceServletContextListener() {
      @Override
      protected Injector getInjector() {
        return Guice.createInjector(new OauthAuthorizationServerModule(""), new InMemoryModule());
      }
    });

    server.setHandler(context);
    server.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          server.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}