package com.clouway.oauth2.app;

import com.clouway.oauth2.exampleapp.OauthAuthorizationServerModule;
import com.clouway.oauth2.exampleapp.storage.InMemoryModule;
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
public class AppServer {
  private final Server server;

  public AppServer(Integer port) {
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
        return Guice.createInjector(new OauthAuthorizationServerModule("", 60 * 60l), new InMemoryModule());
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