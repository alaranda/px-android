package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.mockContextLibDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockKycVaultV2Dao;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPreferenceTidyAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.dto.preference.InitPreferenceRequest;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
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
  private static final String USER_ID_COW = "220115205";

  @Test
  public void getPreference_collectorMeliEmailPayerDistinctEmailPref_ValidationException()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/kyc/user_22314151_cuil_20147360194.json")));

    try {
      PreferenceService.INSTANCE.getPreference(mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_2);
      fail("ValidationException pref");
    } catch (ValidationException e) {
      assertThat(e.getDescription(), is("No podés pagar con este link de pago."));
    }
  }

  @Test
  public void getPreference_collectorMeli_kycApiFailed_returnPreference()
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
  public void getPreference_collectorMeliEmailPayerEqualsEmailPref()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/kyc/collector_email_equal_payer_email.json")));

    final Preference preference =
        PreferenceService.INSTANCE.getPreference(
            mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_1);

    assertNotNull(preference);
  }

  @Test
  public void getPreference_collectorMeli_kycResponseErrorInBody()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/kyc/200_OK_with_error_in_body.json")));

    final Preference preference =
        PreferenceService.INSTANCE.getPreference(
            mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_1);

    assertNotNull(preference);
  }

  @Test
  public void getPreference_collectorMeli_kycNoRegistrationIdentifiers()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/kyc/user_11111111_dni_45464778.json")));

    final Preference preference =
        PreferenceService.INSTANCE.getPreference(
            mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_1);

    assertNotNull(preference);
  }

  @Test
  public void getPreference_preferenceAPIFailed_throwException() throws IOException {

    MockPreferenceAPI.getById(
        PREF_MELICOLLECTOR,
        HttpStatus.SC_BAD_GATEWAY,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    try {
      PreferenceService.INSTANCE.getPreference(mockContextLibDto(), PREF_MELICOLLECTOR, USER_ID_1);
      fail("Should throw ApiException");
    } catch (ApiException e) {
      assertEquals("external_error", e.getCode());
      assertEquals("API call to preference failed", e.getDescription());
    } catch (Exception e) {
      fail("Should throw ApiException");
    }
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
        PreferenceService.INSTANCE.getPreferenceResponse(
            mockContextLibDto(), initPreferenceRequest);
    assertNotNull(preferenceResponse);
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
      PreferenceService.INSTANCE.getPreferenceResponse(mockContextLibDto(), initPreferenceRequest);
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
      PreferenceService.INSTANCE.getPreferenceResponse(mockContextLibDto(), initPreferenceRequest);
      fail("ApiException expected");
    } catch (ApiException e) {
      assertEquals(e.getDescription(), "Error getting parameters");
    }
  }

  @Test
  public void getPreference_whitelistCOWContainsCollector_returnFlowId()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        "856777777-0f03b540-a8c2-4879-af10-66f619786c0c",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/856777777-0f03b540-a8c2-4879-af10-66f619786c0c.json")));

    MockPublicKeyAPI.getBycallerIdAndClientId(
        USER_ID_COW,
        "123",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));

    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn(null);
    when(initPreferenceRequest.getCallerId()).thenReturn(USER_ID_COW);
    when(initPreferenceRequest.getPrefId())
        .thenReturn("856777777-0f03b540-a8c2-4879-af10-66f619786c0c");
    final PreferenceResponse preferenceResponse =
        PreferenceService.INSTANCE.getPreferenceResponse(
            mockContextLibDto(), initPreferenceRequest);

    assertEquals(preferenceResponse.getFlowId(), "/checkout_web");
  }

  @Test
  public void getPreference_whitelistCOWContainsClient_returnFlowId()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        "856777777-0f03b540-a8c2-4879-af10-66f619786c0d",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/856777777-0f03b540-a8c2-4879-af10-66f619786c0d.json")));

    MockPublicKeyAPI.getBycallerIdAndClientId(
        USER_ID_COW,
        "123",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));

    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn(null);
    when(initPreferenceRequest.getCallerId()).thenReturn(USER_ID_COW);
    when(initPreferenceRequest.getPrefId())
        .thenReturn("856777777-0f03b540-a8c2-4879-af10-66f619786c0d");
    final PreferenceResponse preferenceResponse =
        PreferenceService.INSTANCE.getPreferenceResponse(
            mockContextLibDto(), initPreferenceRequest);

    assertEquals(preferenceResponse.getFlowId(), "/checkout_web");
  }

  @Test
  public void getPreference_requestContainsFlowId_returnFlowId()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        "856777777-0f03b540-a8c2-4879-af10-66f619786c0e",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/856777777-0f03b540-a8c2-4879-af10-66f619786c0e.json")));

    MockPublicKeyAPI.getBycallerIdAndClientId(
        USER_ID_COW,
        "123",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));

    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn(null);
    when(initPreferenceRequest.getCallerId()).thenReturn("123");
    when(initPreferenceRequest.getPrefId())
        .thenReturn("856777777-0f03b540-a8c2-4879-af10-66f619786c0e");
    when(initPreferenceRequest.getFlowId()).thenReturn("/param_flow_id");
    final PreferenceResponse preferenceResponse =
        PreferenceService.INSTANCE.getPreferenceResponse(
            mockContextLibDto(), initPreferenceRequest);

    assertEquals(preferenceResponse.getFlowId(), "/param_flow_id");
  }

  @Test
  public void getPreference_additionalInfoContainsFlowId_returnFlowId()
      throws InterruptedException, ApiException, ExecutionException, IOException {

    MockPreferenceAPI.getById(
        "856777777-0f03b540-a8c2-4879-af10-66f619786c0e",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/856777777-0f03b540-a8c2-4879-af10-66f619786c0e.json")));

    MockPublicKeyAPI.getBycallerIdAndClientId(
        USER_ID_COW,
        "123",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));

    InitPreferenceRequest initPreferenceRequest = Mockito.mock(InitPreferenceRequest.class);
    when(initPreferenceRequest.getShortId()).thenReturn(null);
    when(initPreferenceRequest.getCallerId()).thenReturn("123");
    when(initPreferenceRequest.getPrefId())
        .thenReturn("856777777-0f03b540-a8c2-4879-af10-66f619786c0e");
    final PreferenceResponse preferenceResponse =
        PreferenceService.INSTANCE.getPreferenceResponse(
            mockContextLibDto(), initPreferenceRequest);

    assertEquals(preferenceResponse.getFlowId(), "/payment_link");
  }
}
