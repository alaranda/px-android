package com.mercadolibre.endpoints;

import com.mercadolibre.api.MockAccessTokenAPI;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.mocks.MockPreferenceTidyAPI;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

public class PreferenceRouterTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    public static final String ACCES_TOKEN  = "APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610";
    public static final String PREF_ID_INVALID  = "138275050-21ff9440-f9ab-4467-8ad7-c2847c064014";


    @Test
    public void initCheckout_invalidParams_400() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference");


        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void initCheckout_invalidPref_400() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.PREF_ID, PREF_ID_INVALID)
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN);

        MockPreferenceAPI.getById(PREF_ID_INVALID, HttpStatus.SC_NOT_FOUND,
                IOUtils.toString(getClass().getResourceAsStream("/preference/1.json")));
        MockAccessTokenAPI.getAccessToken(ACCES_TOKEN, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/accesToken/APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void initCheckout_validPref_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.PREF_ID, PREF_ID_INVALID)
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN);

        MockPublicKeyAPI.getBycallerIdAndClientId("395662610", 4190463107814393L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockPreferenceAPI.getById(PREF_ID_INVALID, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
        MockAccessTokenAPI.getAccessToken(ACCES_TOKEN, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/accesToken/APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610.json")));
        MockPreferenceTidyAPI.getPreferenceByKey("23BYCZ", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/23BYCZ.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getBody().print(), is(notNullValue()));
    }

    @Test
    public void initCheckout_invalidPreferenceTidySucces_500() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.PREF_ID, PREF_ID_INVALID)
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN);

        MockPreferenceTidyAPI.getPreferenceByKey("23BYCZ", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/200InvalidPreference.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

}
