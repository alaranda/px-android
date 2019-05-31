package com.mercadolibre.utils;

import org.junit.Test;

import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class AccesTokenUtilsTest {
    @Test
    public void hideAccessTokenSensitiveData_accesTokenOk_patternOk() {
        final String testAccessToken = "APP_USR-6519316523937000-070000-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748000";
        final String publicAccessToken = AccessTokenUtils.hideAccessTokenSensitiveData(testAccessToken);
        assertThat(publicAccessToken, is("APP_USR-6519316523937000-070000-****-261748000"));
    }

    @Test
    public void hideAccessTokenSensitiveData_invalidPAccesToken_patternFalse() {
        final String testAccessToken = "APP_USR-6519316523937000-070000-964cafe-7e2c9-1a2c740155fcb5474280__LA_LD__-261748000";
        final String publicAccessToken = AccessTokenUtils.hideAccessTokenSensitiveData(testAccessToken);
        assertThat(publicAccessToken, is(testAccessToken));
    }

    @Test
    public void hideAccessTokenSensitiveData_nullValue_null() {
        final String publicAccessToken = AccessTokenUtils.hideAccessTokenSensitiveData(null);
        assertThat(publicAccessToken, is(nullValue()));
    }

    @Test
    public void accessTokenJsonParam() {
        final String validJsonBody1 = "{  \n" +
                "   \"test\": \"test\",\n" +
                "   \"access_token\":\"APP_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045\",\n" +
                "   \"tessst2\": \"tessst2\"\n" +
                "}";
        final String validJsonBody2 = "{  \n" +
                "   \"test\": \"test\",\n" +
                "   \"access_token\":\"APP_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045\"\n" +
                "}";
        final String validJsonBody3 = "{  \n" +
                "   \"access_token\":\"APP_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045\",\n" +
                "   \"tessst2\": \"tessst2\"\n" +
                "}";

        final String validJsonBody4 = "{  \n" +
                "   \"access_token\":\"APP_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045\",\n" +
                "}";

        final String invalidJsonBody = "{  \n" +
                "   \"test\": \"test\",\n" +
                "   \"access_token_\":\"APP_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045\",\n" +
                "   \"tessst2\": \"tessst2\"\n" +
                "}";

        Matcher matcher = AccessTokenUtils.ACCESS_TOKEN_JSON_PARAM_PATTERN.matcher(validJsonBody1);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_JSON_PARAM_PATTERN.matcher(validJsonBody2);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_JSON_PARAM_PATTERN.matcher(validJsonBody3);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_JSON_PARAM_PATTERN.matcher(validJsonBody4);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_JSON_PARAM_PATTERN.matcher(invalidJsonBody);
        assertThat(matcher.find(), is(false));
    }

    @Test
    public void accessTokenQueryParamPattern_strignAT_true() {
        final String validQueryString1 = "amount=10.0&support_plugins=account_money&access_token=TEST_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045";
        final String validQueryString2 = "access_token=TEST_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045&amount=10.0&support_plugins=account_money";
        final String validQueryString3 = "support_plugins=account_money&access_token=TEST_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045&public_key=amount=10.0";
        final String invalidQueryString = "support_plugins=account_money&access_token=TEST_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045&public_key=amount=10.0";

        Matcher matcher = AccessTokenUtils.ACCESS_TOKEN_QUERY_PARAM_PATTERN.matcher(validQueryString1);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_QUERY_PARAM_PATTERN.matcher(validQueryString2);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_QUERY_PARAM_PATTERN.matcher(validQueryString3);
        assertThat(matcher.find(), is(true));

        matcher = AccessTokenUtils.ACCESS_TOKEN_QUERY_PARAM_PATTERN.matcher(invalidQueryString);
        assertThat(matcher.find(), is(true));
    }

}
