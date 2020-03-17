package com.mercadolibre.utils;


import com.mercadolibre.constants.Constants;

import java.util.regex.Pattern;

import static com.mercadolibre.constants.QueryParamsConstants.ACCESS_TOKEN;

/**
 * Access Token Utils
 */
public class AccessTokenUtils {
    // todo update in toolkit and replace
    private AccessTokenUtils() {
        throw new AssertionError();
    }

    /**
     * Access token query param pattern
     */
    public static final Pattern ACCESS_TOKEN_QUERY_PARAM_PATTERN = Pattern.compile("(?<=([?&;]|^)" + ACCESS_TOKEN + "=).*?(?=($|[&;]))");

    /**
     * Access json body pattern
     */
    public static final Pattern ACCESS_TOKEN_JSON_PARAM_PATTERN = Pattern.compile("\"access_token\"\\s*:\\s*\"((\\\\\"|[^\"])*)\"");

    /**
     * Hides the sensitive data from an access token
     *
     * @param accessToken access token
     * @return String Access token with hidden sensitive data
     */
    public static String hideAccessTokenSensitiveData(final String accessToken) {
        if (accessToken != null && accessToken.matches("^(APP_USR|TEST|ADM)-([0-9]+)-([0-9]+)-([^-]+)-([0-9]+)$")) {
            final String[] splitAT = accessToken.split("-");
            return String.format("%s-%s-%s-****-%s", splitAT[0], splitAT[1], splitAT[2], splitAT[4]);
        }

        return accessToken;
    }
}
