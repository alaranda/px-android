package com.mercadolibre.utils;

import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.utils.newRelic.NewRelicUtils;
import org.junit.Before;
import org.junit.Test;
import spark.Request;

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewRelicUtilsTest {

    Request requestMock;

    @Before
    public void setUp() {
        requestMock = mock(Request.class);
    }

    @Test
    public void noticeError_callWithException_noErrors() {
        when(requestMock.attribute(REQUEST_ID)).thenReturn("test-id");
        final Exception exception = new Exception("Test Exception");
        NewRelicUtils.noticeError(exception, requestMock);
    }

    @Test
    public void noticeError_callWithApiException_noErrors() {
        when(requestMock.attribute(REQUEST_ID)).thenReturn("test-id");
        final Exception exception = new Exception("Test Exception");
        final ApiException apiException = new ApiException("test_code", "test_description", 200, exception);
        NewRelicUtils.noticeError(apiException, requestMock);
    }

}
