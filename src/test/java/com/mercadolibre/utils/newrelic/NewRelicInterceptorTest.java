package com.mercadolibre.utils.newrelic;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.ParseException;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.interceptor.ResponseInterceptor;
import com.mercadolibre.utils.newRelic.NewRelicInterceptor;
import com.newrelic.api.agent.Trace;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewRelicInterceptorTest {

    private NewRelicInterceptor newRelicInterceptor;

    @Before
    public void onSetup() {
        this.newRelicInterceptor = new NewRelicInterceptor();
    }

    @Test
    public void testIntercept_postRequest_addTaskToDeque() {
        Request request = Mockito.mock(Request.class);

        Deque<ResponseInterceptor> deque = new LinkedList<>();

        when(request.getPlainURL()).thenReturn("test");
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getResponseInterceptors()).thenReturn(deque);

        this.newRelicInterceptor.intercept(request);

        assertThat(deque.size(), equalTo(1));
    }

    @Test(expected = IllegalStateException.class)
    public void testIntercept_postRequest_throwsIllegalStateException() {
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);

        Deque<ResponseInterceptor> deque = new LinkedList<>();

        when(request.getPlainURL()).thenReturn("invalid\\url");
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getResponseInterceptors()).thenReturn(deque);

        this.newRelicInterceptor.intercept(request);
        assertThat(deque.size(), equalTo(1));

        deque.getLast().intercept(response);
    }
}
