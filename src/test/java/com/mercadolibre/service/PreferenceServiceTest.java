package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.mockContextLibDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockKycVaultDao;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPreferenceTidyAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.dto.preference.InitPreferenceRequest;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.utils.IOUtils;

public class PreferenceServiceTest {

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  private static final String PREF_MELICOLLECTOR = "127330977-0f03b540-a8c2-4879-af10-66f619786c0c";
  private static final String USER_ID_1 = "243962506";
  private static final String USER_ID_2 = "453962577";
  public static final String REQUEST_ID = UUID.randomUUID().toString();

  @Test
  public void getPreference_collectorMeliEmailPayerDistincEmailPref_ValidationException()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockKycVaultDao.getSensitiveUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/kyc/userSensitiveData.json")));

    try {
      final Preference preference =
          PreferenceService.INSTANCE.getPreference(
              mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_2);
      fail("ValidationException pref");
    } catch (ValidationException e) {
      assertThat(e.getDescription(), is("No podés pagar con este link de pago."));
    }
  }

  @Test
  public void getPreference_collectorMeliEmailPayerEqualsEmailPref_200()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    final Preference preference =
        PreferenceService.INSTANCE.getPreference(
            mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_1);

    assertNotNull(preference);
  }

  @Test
  public void extractParamPrefId_ifShortIdIsNotNullThenUsesShortId_success()
      throws IOException, ApiException, ExecutionException, InterruptedException {
    MockPreferenceTidyAPI.getPreferenceByKey(
        "45AASS",
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/45AASS.json")));

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockPublicKeyAPI.getBycallerIdAndClientId(
        "220115205",
        "48638295529722",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));

    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn("45AASS");
    when(initPreferenceRequest.getPrefId()).thenReturn(null);
    when(initPreferenceRequest.getCallerId()).thenReturn("12345");
    PreferenceResponse preferenceResponse =
        PreferenceService.INSTANCE.getPreferenceResponce(
            mockContextLibDto(), initPreferenceRequest);
    assertTrue(preferenceResponse != null);
    assertEquals(preferenceResponse.getPrefId(), "127330977-0f03b540-a8c2-4879-af10-66f619786c0c");
    assertEquals(preferenceResponse.getPublicKey(), "APP_USR-b96cf47b-cbb2-4c8c-83cb-a8cb01167b4e");
  }

  @Test
  public void extractParamPrefId_ifShortIdIsNotNullThenUsesShortId_fails()
      throws IOException, ExecutionException, InterruptedException {
    MockPreferenceTidyAPI.getPreferenceByKey(
        "23BYCZ",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preferenceTidy/200InvalidPreference.json")));

    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn("23BYCZ");
    when(initPreferenceRequest.getPrefId()).thenReturn(null);
    try {
      PreferenceService.INSTANCE.getPreferenceResponce(mockContextLibDto(), initPreferenceRequest);
      fail("ApiException expected");
    } catch (ApiException e) {
      assertEquals(e.getDescription(), "Error getting parameters");
    }
  }

  @Test
  public void extractParamPrefId_ifShortIdIsNullAndPrefIdIsNull_fails()
      throws ExecutionException, InterruptedException {
    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn(null);
    when(initPreferenceRequest.getPrefId()).thenReturn(null);
    try {
      PreferenceService.INSTANCE.getPreferenceResponce(mockContextLibDto(), initPreferenceRequest);
      fail("ApiException expected");
    } catch (ApiException e) {
      assertEquals(e.getDescription(), "Error getting parameters");
    }
  }
}
