package com.mercadolibre.utils.logs;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;

import java.util.Optional;

import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;

public class RequestLogUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void logRawRequest(Request request) {

        final Optional<String> queryParamsOpt = LogUtils.getQueryParams(request.queryString());
        final String queryParams = queryParamsOpt.isPresent() ? queryParamsOpt.get() : "";

        LOGGER.info(LogUtils.getRequestLog(request.attribute(Constants.REQUEST_ID),
                request.requestMethod(), request.url(), request.userAgent(),
                request.headers(SESSION_ID), queryParams, request.body()
        ));
    }
}
