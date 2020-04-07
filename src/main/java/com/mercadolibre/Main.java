package com.mercadolibre;

import static spark.embeddedserver.EmbeddedServers.Identifiers.JETTY;

import com.mercadolibre.router.Router;
import com.mercadolibre.security.authentication.filters.impl.ExternalAuthenticationFilter;
import java.io.IOException;
import javax.servlet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import spark.Spark;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyServer;
import spark.embeddedserver.jetty.JettyHandler;
import spark.http.matching.MatcherFilter;

public class Main {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final int FURY_PORT = 8080;

  public static void main(String[] args) {
    setupSparkServer();
    Spark.port(FURY_PORT);
    new Router().init();
    Spark.awaitInitialization();
    LOGGER.info("Listening on port " + FURY_PORT);
  }

  private static void setupSparkServer() {
    EmbeddedServers.add(
        JETTY,
        (routeMatcher, staticFilesConfiguration, hasMultipleHandler) -> {
          // Create a standard Spark handler (taken from Spark's EmbeddedJettyFactory)
          final MatcherFilter matcherFilter =
              new MatcherFilter(routeMatcher, staticFilesConfiguration, false, hasMultipleHandler);
          matcherFilter.init(null);

          return new EmbeddedJettyServer(
              (int maxThreads, int minThreads, int threadTimeoutMillis) -> new Server(),
              new JettyHandler(decorateWithMLAuth(matcherFilter)));
        });
  }

  private static Filter decorateWithMLAuth(final Filter decoratedFilter) {
    return new Filter() {
      private final Filter mlAuthFilter = new ExternalAuthenticationFilter();

      @Override
      public void init(final FilterConfig filterConfig) throws ServletException {
        mlAuthFilter.init(filterConfig);
      }

      @Override
      public void doFilter(
          final ServletRequest request, final ServletResponse response, final FilterChain chain)
          throws IOException, ServletException {
        mlAuthFilter.doFilter(
            request, response, (req, res) -> decoratedFilter.doFilter(req, res, chain));
      }

      @Override
      public void destroy() {
        mlAuthFilter.destroy();
      }
    };
  }
}
