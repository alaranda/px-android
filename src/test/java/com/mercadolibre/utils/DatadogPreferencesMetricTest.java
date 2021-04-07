package com.mercadolibre.utils;

import static org.mockito.Mockito.when;

import com.mercadolibre.dto.preference.PreferenceResponse;
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
  final Context context = Mockito.mock(Context.class);

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void addPreferenceDataTest_validPreference_allInfo() {
    PreferenceResponse preferenceResponse = Mockito.mock(PreferenceResponse.class);
    when(preferenceResponse.getFlowId()).thenReturn("/multiplayer");
    when(preferenceResponse.getProductId()).thenReturn("123");

    PublicKey publicKey = Mockito.mock(PublicKey.class);
    when(publicKey.getSiteId()).thenReturn("MLA");

    when(context.getSite()).thenReturn(Site.MLA);
    when(context.getPlatform()).thenReturn(Platform.MP);

    DatadogPreferencesMetric.addPreferenceData(context, preferenceResponse);
  }

  @Test
  public void addPreferenceDataTest_invalidPreference_allInfo() {
    Preference preference = Mockito.mock(Preference.class);
    when(preference.getOperationType()).thenReturn("op_type");
    when(preference.getMarketplace()).thenReturn("ML");

    Context context = Mockito.mock(Context.class);
    when(context.getSite()).thenReturn(Site.MLA);
    when(context.getFlow()).thenReturn("px_flow");
    when(context.getPlatform()).thenReturn(Platform.MP);

    DatadogPreferencesMetric.addInvalidPreferenceData(context, preference);
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

    DatadogPreferencesMetric.addInvalidPreferenceData(context, preference);
  }
}
