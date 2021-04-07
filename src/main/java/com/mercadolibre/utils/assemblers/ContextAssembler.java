package com.mercadolibre.utils.assemblers;

import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.constants.HeadersConstants.*;
import static com.mercadolibre.utils.HeadersUtils.userAgentFromHeader;
import static org.apache.http.protocol.HTTP.USER_AGENT;

import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.restclient.util.MeliContextBuilder;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.utils.StringUtils;

public class ContextAssembler {

  public static Context toContext(final Request request) {

    final Map<String, String> metrics = new HashMap<>();

    Context.ContextBuilder builder =
        Context.builder()
            .meliContext(MeliContextBuilder.build(request.raw()))
            .requestId(request.attribute(HeadersConstants.X_REQUEST_ID))
            .metrics(metrics);

    final String userAgentHeader = request.headers(USER_AGENT);
    builder.userAgent(userAgentFromHeader(userAgentHeader));

    final String flowHeader = request.headers(FLOW_ID);
    if (flowHeader != null) {
      builder.flow(flowHeader);
    }

    final String siteId = request.queryParams(CALLER_SITE_ID);
    final String languageHeader = request.headers(LANGUAGE);
    builder.locale(languageHeader, siteId);

    final String platformHeader = request.headers(PLATFORM);
    if (StringUtils.isNotBlank(platformHeader)) {
      try {
        final Platform platform = Platform.from(platformHeader);
        builder.platform(platform);
      } catch (IllegalArgumentException e) {
        // do nothing. this header may have gibberish
      }
    }

    return builder.build();
  }
}
