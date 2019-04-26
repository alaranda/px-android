package com.mercadolibre.router;

import com.mercadolibre.constants.Constants;
import spark.utils.StringUtils;

public final class ApiContext {

    public static String getApiContextFromScope(final String scope) {
        if (StringUtils.isNotEmpty(scope)) {
            switch (scope.toLowerCase()) {
                case Constants.SCOPE_PROD:
                    return Constants.API_CONTEXT_V1;
                case Constants.SCOPE_BETA:
                    return  Constants.SCOPE_BETA;
                default:
                    return Constants.SCOPE_ALPHA;
            }
        }
        return Constants.SCOPE_ALPHA;
    }
}
