package com.mercadolibre.service;

import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPreferenceTidyAPI;
import com.mercadolibre.api.MockUserAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class PreferenceServiceTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    private static final String PREF_MELICOLLECTOR = "127330977-0f03b540-a8c2-4879-af10-66f619786c0c";
    private static final Long USER_ID_1 = 243962506L;
    private static final Long USER_ID_2 = 453962577L;
    public static final String REQUEST_ID = UUID.randomUUID().toString();
    public static final Context CONTEXT_ES = Context.builder().requestId(REQUEST_ID).locale("es-AR").platform(Platform.MP).build();

    @Test
    public void getPreference_collectorMeliEmailPayerDistincEmailPref_ValidationException() throws IOException, InterruptedException, ApiException, ExecutionException {

        MockPreferenceAPI.getById(PREF_MELICOLLECTOR, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));
        MockUserAPI.getById(USER_ID_2, 200,  IOUtils.toString(getClass().getResourceAsStream("/user/453962577.json")));

        try {
            final Preference preference = PreferenceService.INSTANCE.getPreference(CONTEXT_ES, PREF_MELICOLLECTOR, USER_ID_2);
            fail("ValidationException pref");
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is("No podés pagar con este link de pago."));
        }
    }

    @Test
    public void getPreference_collectorMeliEmailPayerEqualsEmailPref_200() throws InterruptedException, ApiException, ExecutionException, IOException {

        MockPreferenceAPI.getById(PREF_MELICOLLECTOR, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));
        MockUserAPI.getById(USER_ID_1, 200,  IOUtils.toString(getClass().getResourceAsStream("/user/243962506.json")));;

        final Preference preference = PreferenceService.INSTANCE.getPreference(CONTEXT_ES, PREF_MELICOLLECTOR, USER_ID_1);

        assertNotNull(preference);
    }

    @Test
    public void getClientId_setClientIdAT_clientIdAT() {

        final Long clientID = PreferenceService.INSTANCE.getClientId(963L, 10101010L);
        assertThat(clientID, is(10101010L));
    }

    @Test
    public void getClientId_setClientIdPref_clientIdPref() {

        final Long clientID = PreferenceService.INSTANCE.getClientId(456789L, 10101010L);
        assertThat(clientID, is(456789L));
    }

    @Test
    public void extractParamPrefId_ifShortIdIsNullThenReturnsPrefIdFromParam_success() throws ApiException {
        Request request = Mockito.mock(Request.class);
        when(request.queryParams(Constants.SHORT_ID)).thenReturn("");
        when(request.queryParams(Constants.PREF_ID)).thenReturn(":pref_id");
        String prefId = PreferenceService.INSTANCE.extractParamPrefId(CONTEXT_ES, request);
        assertEquals(prefId, ":pref_id");
    }

    @Test
    public void extractParamPrefId_ifShortIdIsNotNullThenUsesShortId_success() throws IOException, ApiException {
        MockPreferenceTidyAPI.getPreferenceByKey("23BYCZ", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/23BYCZ.json")));

        Request request = Mockito.mock(Request.class);
        when(request.queryParams(Constants.SHORT_ID)).thenReturn("23BYCZ");
        when(request.queryParams(Constants.PREF_ID)).thenReturn(null);
        String response = PreferenceService.INSTANCE.extractParamPrefId(CONTEXT_ES, request);
        assertTrue(!isBlank(response));
        assertEquals(response, "395662610-297aa2ed-4556-4085-859f-726ab9bab51f");
    }

    @Test
    public void extractParamPrefId_ifShortIdIsNotNullThenUsesShortId_fails() throws IOException {
        MockPreferenceTidyAPI.getPreferenceByKey("23BYCZ", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/200InvalidPreference.json")));

        Request request = Mockito.mock(Request.class);
        when(request.queryParams(Constants.SHORT_ID)).thenReturn("23BYCZ");
        when(request.queryParams(Constants.PREF_ID)).thenReturn(null);
        try {
            String response = PreferenceService.INSTANCE.extractParamPrefId(CONTEXT_ES, request);
            fail("ApiException expected");
        } catch (ApiException e) {
            assertEquals(e.getDescription(), "Error getting parameters");
        }
    }

    @Test
    public void extractParamPrefId_ifShortIdIsNullAndPrefIdIsNull_fails() {
        Request request = Mockito.mock(Request.class);
        when(request.queryParams(Constants.SHORT_ID)).thenReturn(null);
        when(request.queryParams(Constants.PREF_ID)).thenReturn(null);
        try {
            PreferenceService.INSTANCE.extractParamPrefId(CONTEXT_ES, request);
            fail("ApiException expected");
        } catch (ApiException e) {
            assertEquals(e.getDescription(), "Error getting parameters");
        }
    }

}
