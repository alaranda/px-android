package com.mercadolibre.utils;

import static com.mercadolibre.constants.QueryParamsConstants.ACCESS_TOKEN;

import java.util.regex.Pattern;

/** Access Token Utils */
public class AccessTokenUtils {
  /** Access token query param pattern */
  public static final Pattern ACCESS_TOKEN_QUERY_PARAM_PATTERN =
      Pattern.compile("(?<=([?&;]|^)" + ACCESS_TOKEN + "=).*?(?=($|[&;]))");
  /** Access json body pattern */
  public static final Pattern ACCESS_TOKEN_JSON_PARAM_PATTERN =
      Pattern.compile("\"access_token\"\\s*:\\s*\"((\\\\\"|[^\"])*)\"");

  private static final String ACCESS_TOKEN_PATTERN =
      "^(APP_USR|TEST|ADM)-([0-9]+)-([0-9]+)-([^-]+)-([0-9]+)$";

  // todo update in toolkit and replace
  private AccessTokenUtils() {
    throw new AssertionError();
  }

  /**
   * Hides the sensitive data from an access token
   *
   * @param accessToken access token
   * @return String Access token with hidden sensitive data
   */
  public static String hideAccessTokenSensitiveData(final String accessToken) {
    if (validateAccessToken(accessToken)) {
      final String[] splitAT = accessToken.split("-");
      return String.format("%s-%s-%s-****-%s", splitAT[0], splitAT[1], splitAT[2], splitAT[4]);
    }

    return accessToken;
  }

  /**
   * Extracts the user ID value from an access token
   *
   * @param accessToken access token
   * @return Long User ID
   */
  public static Long extractUserIdFromAccessToken(final String accessToken) {
    if (validateAccessToken(accessToken)) {
      final String[] splitAT = accessToken.split("\\-");
      return Long.parseLong(splitAT[4]);
    }

    return null;
  }

  /**
   * Validate format of an access token
   *
   * @param accessToken access token
   * @return Boolean valid or not
   */
  public static Boolean validateAccessToken(final String accessToken) {
    return accessToken != null && accessToken.matches(ACCESS_TOKEN_PATTERN);
  }
}
