package com.mercadolibre.service;

import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.merch.Content;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OnDemandResourcesTest extends RestClientTestBase {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    private static final OnDemandResources onDemandResources = OnDemandResources.INSTANCE;

    public static final Locale LOCALE_ES = Locale.forLanguageTag("es-AR");


    @Test
    public void createOnDemandResoucesUrlByContent_fail() {

        final CongratsRequest congratsRequest = new CongratsRequest(null, null,null,
                null, null, null, "density", null, null, null);

        String response = onDemandResources.createOnDemandResoucesUrlByContent(congratsRequest, null, LOCALE_ES);
        assertThat(response, nullValue());
    }

    @Test
    public void createOnDemandResoucesUrlByContent_success() {

        final CongratsRequest congratsRequest = new CongratsRequest(null, null,null,
                null, null, null, "density", null, null, null);
        final Content content = Mockito.mock(Content.class);
        when(content.getIcon()).thenReturn("icon");

        String response = onDemandResources.createOnDemandResoucesUrlByContent(congratsRequest, content, LOCALE_ES);
        assertTrue(response.contains("remote_resources/image/"));
    }
}
