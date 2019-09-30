package com.mercadolibre.utils.newRelic;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.interceptor.RequestInterceptor;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;


public final class NewRelicInterceptor implements RequestInterceptor {

    @Override
    public void intercept(final Request request) {
        // we start a segment on the current transaction thread
        final Segment segment = NewRelic.getAgent().getTransaction().startSegment("apiCall");
        request.getResponseInterceptors().addLast(response -> {
            try {
                final ExternalParameters parameters = HttpParameters.library("MeliRestClient")
                        .uri(buildReportingUri(request, response)).procedure(request.getMethod().name())
                        .noInboundHeaders().build();
                // we report the segment as an external service on the response thread
                segment.reportAsExternal(parameters);
            } catch (final URISyntaxException e) {
                throw new IllegalStateException(e);
            } finally {
                segment.end();
            }
        });
    }

    private static URI buildReportingUri(final Request request, final Response response) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(request.getPlainURL());
        final Header nginxPoolHeader = response.getHeader("x-nginx-pool");
        if (nginxPoolHeader != null) {
            uriBuilder.setHost(nginxPoolHeader.getValue());
        }
        return uriBuilder.build();
    }
}