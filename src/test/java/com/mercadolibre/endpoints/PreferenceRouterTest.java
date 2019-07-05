package com.mercadolibre.endpoints;

import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.mocks.MockPreferenceTidyAPI;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PreferenceRouterTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    public static final String PREF_ID_INVALID  = "138275050-21ff9440-f9ab-4467-8ad7-c2847c064014";
    public static final String PREF_VALID = "395662610-297aa2ed-4556-4085-859f-726ab9bab51f";
    public static final String PREF_ID_WITHOUT_SHIPMENT_NODE = "dde1cff2-0a52-45ca-bc7b-bbd7360128d5";
    public static final String SHORT_ID = "23BYCZ";


    @Test
    public void initCheckout_invalidParams_400() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.CLIENT_ID_PARAM, "395662610");

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void initCheckout_invalidPref_400() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.PREF_ID, PREF_ID_INVALID)
                .addParameter(Constants.CLIENT_ID_PARAM, "395662610");

        MockPreferenceAPI.getById(PREF_ID_INVALID, HttpStatus.SC_NOT_FOUND,
                IOUtils.toString(getClass().getResourceAsStream("/preference/preferenceNotFound.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void initCheckout_validPref_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.SHORT_ID, SHORT_ID)
                .addParameter(Constants.CLIENT_ID_PARAM, "395662610");

        MockPublicKeyAPI.getBycallerIdAndClientId("395662610", 4190463107814393L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
        MockPreferenceAPI.getById(PREF_VALID, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
        MockPreferenceTidyAPI.getPreferenceByKey("23BYCZ", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/23BYCZ.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getBody().print(), is(notNullValue()));
    }

    @Test
    public void initCheckout_invalidPreferenceTidySucces_500() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.PREF_ID, PREF_ID_INVALID);

        MockPreferenceTidyAPI.getPreferenceByKey("23BYCZ", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/200InvalidPreference.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    public void initCheckout_validOldPrefWithoutShipmentsNode_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/init/preference")
                .addParameter(Constants.PREF_ID, PREF_ID_WITHOUT_SHIPMENT_NODE)
                .addParameter(Constants.CLIENT_ID_PARAM, "395662610");

        MockPublicKeyAPI.getBycallerIdAndClientId("395662610", 4190463107814393L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
        MockPreferenceAPI.getById(PREF_ID_WITHOUT_SHIPMENT_NODE, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/dde1cff2-0a52-45ca-bc7b-bbd7360128d5.json")));

        final Response response = get(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getBody().print(), is(notNullValue()));
    }

}
