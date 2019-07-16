package com.mercadolibre.utils.newRelic;

import com.google.common.collect.ImmutableSet;
import com.mercadolibre.exceptions.ApiException;
import com.newrelic.api.agent.NewRelic;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;

/**
 * New relic utility class
 */
public final class NewRelicUtils {

    private static final Set<Integer> EXPECTED_STATUS_CODES = ImmutableSet.of(400,401,403,404,405,409);

    /**
     * Notices error to new relic using exception for stack trace, specified parameters and request for request ID
     *
     * @param exception    exceptions
     * @param paramsToShow Parameters to show in new relix
     * @param request      Request
     */
    private static void noticeError(final Exception exception, final Map<String, String> paramsToShow, final Request request, final boolean expected) {
        final Map<String, String> parameters = new HashMap();
        parameters.put("request-id", request.attribute(REQUEST_ID));
        if (paramsToShow != null && !paramsToShow.isEmpty()) {
            parameters.putAll(paramsToShow);
        }
        NewRelic.noticeError(exception, parameters, expected);
    }

    /**
     * Notices an error related to an exception
     *
     * @param exception exception
     * @param request   request
     */
    public static void noticeError(final Exception exception, final Request request) {
        noticeError(exception, null, request, false);
    }

    /**
     * Notices an error related to an api exception
     *
     * @param exception exception
     * @param request   request
     */
    public static void noticeError(final ApiException exception, final Request request) {
        noticeError(exception, exception.getNewRelicParams(), request, EXPECTED_STATUS_CODES.contains(exception.getStatusCode()));
    }
}
