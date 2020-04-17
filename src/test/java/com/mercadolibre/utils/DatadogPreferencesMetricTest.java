package com.mercadolibre.utils;

import static org.mockito.Mockito.when;

import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DatadogPreferencesMetricTest {

  final UserAgent USER_AGENT_IOS = UserAgent.create("PX/iOS/4.5.0");

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void testDatadogPreferencesMetricTagsIOS() {
    Preference preference = Mockito.mock(Preference.class);
    when(preference.getOperationType()).thenReturn("op_type");
    when(preference.getMarketplace()).thenReturn("ML");

    PublicKey publicKey = Mockito.mock(PublicKey.class);
    when(publicKey.getSiteId()).thenReturn("MLA");

    DatadogPreferencesMetric.addPreferenceData(preference, publicKey, USER_AGENT_IOS.toString());
  }

  @Test
  public void testDatadogPreferencesMetricTags() {
    Preference preference = Mockito.mock(Preference.class);
    when(preference.getOperationType()).thenReturn("op_type");
    when(preference.getMarketplace()).thenReturn("ML");

    Context context = Mockito.mock(Context.class);
    when(context.getSite()).thenReturn(Site.MLA);
    when(context.getFlow()).thenReturn("px_flow");
    when(context.getPlatform()).thenReturn(Platform.MP);

    DatadogPreferencesMetric.addInvalidPreferenceData(preference, context);
  }

  @Test
  public void testDatadogPreferencesMetricEmptyTags() {
    Preference preference = Mockito.mock(Preference.class);
    when(preference.getOperationType()).thenReturn("op_type");
    when(preference.getMarketplace()).thenReturn("ML");

    Context context = Mockito.mock(Context.class);
    when(context.getSite()).thenReturn(null);
    when(context.getFlow()).thenReturn("px_flow");
    when(context.getPlatform()).thenReturn(null);

    DatadogPreferencesMetric.addInvalidPreferenceData(preference, context);
  }
}
