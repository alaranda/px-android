package com.mercadolibre.utils;

import static org.mockito.Mockito.when;

import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.utils.datadog.DatadogCongratsMetric;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DatadogCongratsMetricTest {

  final UserAgent USER_AGENT_IOS = UserAgent.create("PX/iOS/4.5.0");
  final UserAgent USER_AGENT_ANDROID = UserAgent.create("PX/android/4.5.0");

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void testDatadogMetricTagsIOS() {
    CongratsRequest congratsRequest = Mockito.mock(CongratsRequest.class);
    when(congratsRequest.getSiteId()).thenReturn("MLA");
    when(congratsRequest.getPlatform()).thenReturn("ML");
    when(congratsRequest.getProductId()).thenReturn("12352");
    when(congratsRequest.getUserAgent()).thenReturn(USER_AGENT_IOS);
    when(congratsRequest.getFlowName()).thenReturn("PX_FLOW");

    DatadogCongratsMetric.requestCongratsMetric(congratsRequest);
  }

  @Test
  public void testDatadogMetricTagsAndroid() {
    CongratsRequest congratsRequest = Mockito.mock(CongratsRequest.class);
    when(congratsRequest.getSiteId()).thenReturn("MLA");
    when(congratsRequest.getPlatform()).thenReturn("ML");
    when(congratsRequest.getProductId()).thenReturn("12352");
    when(congratsRequest.getUserAgent()).thenReturn(USER_AGENT_ANDROID);
    when(congratsRequest.getFlowName()).thenReturn("PX_FLOW");

    DatadogCongratsMetric.requestCongratsMetric(congratsRequest);
  }
}
