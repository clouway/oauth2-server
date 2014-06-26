package com.example.auth.server;

import com.example.auth.core.CoreModule;
import com.example.auth.http.WebModule;
import com.example.auth.memory.MemoryModule;
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
public class AuthServer {
  private final Server server;

  public AuthServer(Integer port) {
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
        return Guice.createInjector(new CoreModule(), new WebModule(), new MemoryModule());
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