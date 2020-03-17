package com.mercadolibre.router;

import com.newrelic.api.agent.NewRelic;
import org.apache.commons.lang.NullArgumentException;
import spark.Request;
import spark.Response;
import spark.Route;

public class MeteredRoute implements Route {

    private static final String CATEGORY_NAME = "mobile-request";
    private final Route innerRoute;
    private final String transactionName;

    public MeteredRoute(final Route innerRoute, final String transactionName) {
        if (innerRoute == null) {
            throw new NullArgumentException("innerRoute");
        }

        if (transactionName == null) {
            throw new NullArgumentException("transactionName");
        }

        this.innerRoute = innerRoute;
        this.transactionName = transactionName;
    }

    @Override
    public Object handle(final Request request, final Response response) throws Exception {
        NewRelic.setTransactionName(CATEGORY_NAME, this.transactionName);
        Long executionStart = System.currentTimeMillis();
        try {
            return innerRoute.handle(request, response);
        }
        finally {
            Long executionEnd = System.currentTimeMillis();
            NewRelic.recordResponseTimeMetric(this.transactionName, (executionEnd - executionStart));
        }
    }
}