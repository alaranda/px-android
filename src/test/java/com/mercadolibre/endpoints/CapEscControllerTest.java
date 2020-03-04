package com.mercadolibre.endpoints;

import com.mercadolibre.api.MockFraudApi;
import com.mercadolibre.controllers.CapEscController;
import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.constants.CommonParametersNames;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class CapEscControllerTest {

    private CapEscController capEscController = new CapEscController();

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    @Test
    public void resetCapEsc_resetCap_200() throws ApiException {

        MockFraudApi.resetCapEsc("123", "321", 200);

        final Request request = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class);
        when(request.params("cardId")).thenReturn("123");
        when(request.queryParams(CommonParametersNames.CLIENT_ID)).thenReturn("321");

        final ResetStatus resetStatus = capEscController.resetCapEsc(request, response);

        assertThat(resetStatus.getStatus(), is("ok"));
    }

    @Test
    public void resetCapEsc_resetCap_400() throws ApiException {

        final Request request = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class);
        when(request.params("cardId")).thenReturn("123");
        when(request.queryParams(CommonParametersNames.CLIENT_ID)).thenReturn(null);

        try {
            final ResetStatus resetStatus = capEscController.resetCapEsc(request, response);
            fail("ValidationException clientId");
        } catch (ValidationException e) {
            assertThat(e.getMessage(), is("client id is required"));
        }
    }
}
