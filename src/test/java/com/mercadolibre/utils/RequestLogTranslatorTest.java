package com.mercadolibre.utils;

import com.mercadolibre.utils.logs.RequestLogTranslator;
import org.junit.Before;
import org.junit.Test;
import spark.Request;

import java.util.Optional;

import static com.mercadolibre.constants.HeadersConstants.SESSION_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestLogTranslatorTest {
    private Request requestMock;
    private String queryParamsAT, queryParamsATHidden, queryParamsNoAT;
    private String jsonAT, jsonATHidden, jsonNoAT;
    private final String sessionIdValue = "648a260d-6fd9-4ad7-9284-90f2226";;

    @Before
    public void setUp() {
        requestMock = mock(Request.class);

        queryParamsAT = "public_key=APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d&amount=500.0&access_token=APP_USR-6519316523937252-070516-dfdf2343242fdsf_SADF-261748045&excluded_payment_types=atm,digital_currency,ticket&processing_mode=aggregator&cards_esc=223907843,223590400&differential_pricing_id=23&default_installments=3";
        queryParamsATHidden = "public_key=APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d&amount=500.0&access_token=APP_USR-6519316523937252-070516-****-261748045&excluded_payment_types=atm,digital_currency,ticket&processing_mode=aggregator&cards_esc=223907843,223590400&differential_pricing_id=23&default_installments=3";
        queryParamsNoAT = "public_key=APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d&amount=500.0&excluded_payment_types=atm,digital_currency,ticket&processing_mode=aggregator&cards_esc=223907843,223590400&differential_pricing_id=23&default_installments=3";

        jsonAT = "{\n" +
                "    \"public_key\" : \"APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d\",\n" +
                "    \"amount\" : \"500.0\",\n" +
                "    \"access_token\" : \"APP_USR-6519316523937252-070516-dfdf2343242fdsf_SADF-261748045\",\n" +
                "    \"excluded_payment_types\" : \"atm,digital_currency,ticket\",\n" +
                "    \"processing_mode\" : \"aggregator\",\n" +
                "    \"cards_esc\" : \"223907843,223590400\",\n" +
                "    \"differential_pricing_id\" : \"23\",\n" +
                "    \"default_installments\" : \"3\"\n" +
                "}";

        jsonATHidden = "{\n" +
                "    \"public_key\" : \"APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d\",\n" +
                "    \"amount\" : \"500.0\",\n" +
                "    \"access_token\":\"APP_USR-6519316523937252-070516-****-261748045\",\n" +
                "    \"excluded_payment_types\" : \"atm,digital_currency,ticket\",\n" +
                "    \"processing_mode\" : \"aggregator\",\n" +
                "    \"cards_esc\" : \"223907843,223590400\",\n" +
                "    \"differential_pricing_id\" : \"23\",\n" +
                "    \"default_installments\" : \"3\"\n" +
                "}";

        jsonNoAT = "{\n" +
                "    \"public_key\" : \"APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d\",\n" +
                "    \"amount\" : \"500.0\",\n" +
                "    \"excluded_payment_types\" : \"atm,digital_currency,ticket\",\n" +
                "    \"processing_mode\" : \"aggregator\",\n" +
                "    \"cards_esc\" : \"223907843,223590400\",\n" +
                "    \"differential_pricing_id\" : \"23\",\n" +
                "    \"default_installments\" : \"3\"\n" +
                "}";
    }

    @Test
    public void getMethod_delete_delete() {
        when(requestMock.requestMethod()).thenReturn("DELETE");
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        assertThat(requestLogTranslator.getMethod(), is("DELETE"));
    }

    @Test
    public void getUrl_url_url() {
        when(requestMock.url()).thenReturn("unit/test/url");
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        assertThat(requestLogTranslator.getUrl(), is("unit/test/url"));
    }

    @Test
    public void getQueryParams_withAccessToken_ok() {
        when(requestMock.requestMethod()).thenReturn("GET");
        when(requestMock.queryString()).thenReturn(queryParamsAT);
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        Optional<String> queryParamsString = requestLogTranslator.getQueryParams();
        assertThat(queryParamsString.get(), is(queryParamsATHidden));
    }

    @Test
    public void getQueryParams_withoutAccessToken_queryParamsNoAt() {
        when(requestMock.requestMethod()).thenReturn("GET");
        when(requestMock.queryString()).thenReturn(queryParamsNoAT);
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        Optional<String> queryParamsString = requestLogTranslator.getQueryParams();
        assertThat(queryParamsString.get(), is(queryParamsNoAT));
    }

    @Test
    public void getQueryParams_notGetMethod_false() {
        when(requestMock.requestMethod()).thenReturn("POST");
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        Optional<String> queryParamsString = requestLogTranslator.getQueryParams();
        assertThat(queryParamsString.isPresent(), is(false));
    }

    @Test
    public void getBody_post_AtHidden() {
        when(requestMock.requestMethod()).thenReturn("POST");
        when(requestMock.body()).thenReturn(jsonAT);
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        Optional<String> opBodyString = requestLogTranslator.getBody();
        assertThat(opBodyString.get(), is(jsonATHidden));
    }

    @Test
    public void getBody_withoutAccessToken_noAt() {
        when(requestMock.requestMethod()).thenReturn("POST");
        when(requestMock.body()).thenReturn(jsonNoAT);
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        Optional<String> opBodyString = requestLogTranslator.getBody();
        assertThat(opBodyString.get(), is(jsonNoAT));
    }

    @Test
    public void getBodyt_emptyBody_false() {
        when(requestMock.requestMethod()).thenReturn("POST");
        when(requestMock.body()).thenReturn("");
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        Optional<String> opBodyString = requestLogTranslator.getBody();
        assertThat(opBodyString.isPresent(), is(false));
    }

    @Test
    public void requestLogTransaltor_sessionId_sessionIdValue() {
        when(requestMock.requestMethod()).thenReturn("POST");
        when(requestMock.headers(SESSION_ID)).thenReturn(sessionIdValue);
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        assertThat(requestLogTranslator.getSessionId(), is(sessionIdValue));
    }

    @Test
    public void requestLogTransaltor_withoutsessionId_null() {
        when(requestMock.requestMethod()).thenReturn("POST");
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(requestMock);
        assertThat(requestLogTranslator.getSessionId(), is(nullValue()));
    }

}
