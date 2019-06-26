package com.mercadolibre.router;

import com.mercadolibre.constants.Constants;
import spark.utils.StringUtils;

public final class ApiContext {

    public static String getApiContextFromScope(final String scope) {
        if (isInFuryScope(scope)) {
            if (Constants.SCOPE_PROD.equals(scope.toLowerCase())) {
                return Constants.API_CONTEXT_V1;
            }
            return scope;
        }
        return Constants.API_CONTEXT_LOCALHOST;
    }

    public static boolean isInFuryScope(final String scope) {
        return StringUtils.isNotEmpty(scope);
    }
}
