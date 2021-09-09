package com.mercadolibre.utils.assemblers;

import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.SITE_ID;
import static com.mercadolibre.px.constants.HeadersConstants.*;
import static com.mercadolibre.utils.HeadersUtils.userAgentFromHeader;
import static org.apache.http.protocol.HTTP.USER_AGENT;

import com.mercadolibre.constants.QueryParamsConstants;
import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.utils.RestUtils;
import com.mercadolibre.restclient.util.MeliContextBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    } else if (request.queryParams(QueryParamsConstants.FLOW_ID) != null) {
      builder.flow(request.queryParams(QueryParamsConstants.FLOW_ID));
    }

    String siteId = Optional.ofNullable(request.queryParams(CALLER_SITE_ID)).orElse(null);
    final String clientInfo = request.headers(X_CLIENT_INFO);
    if (siteId == null && StringUtils.isNotEmpty(clientInfo)) {
      siteId = RestUtils.getFromClientInfoHeader(clientInfo, SITE_ID).orElse(null);
    }
    if (siteId != null) {
      builder.site(Site.from(siteId));
    }
    final String languageHeader = request.headers(LANGUAGE);
    builder.locale(languageHeader, siteId);

    final String platformHeader = request.headers(PLATFORM);
    if (StringUtils.isNotBlank(platformHeader)) {
      try {
        final Platform platform = Platform.from(platformHeader);
        builder.platform(platform);
      } catch (IllegalStateException e) {
        // do nothing. this header may have gibberish
      }
    }

    return builder.build();
  }
}
