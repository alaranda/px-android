package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.*;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.monitoring.lib.log.LogBuilder.requestInLogBuilder;

import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.api.PreferenceTidyAPI;
import com.mercadolibre.dto.kyc.UserIdentificationResponse;
import com.mercadolibre.dto.preference.AdditionalInfo;
import com.mercadolibre.dto.preference.InitPreferenceRequest;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.dto.preference.PreferenceTidy;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.validators.PreferencesValidator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.CollectionUtils;
import spark.utils.StringUtils;

public enum PreferenceService {
  INSTANCE;

  private static final String SERVICE_NAME = "PreferenceService";
  private static final Logger LOGGER = LogManager.getLogger();

  private final PreferencesValidator PREFERENCES_VALIDATOR = new PreferencesValidator();
  private static final Long DEFAULT_CLIENT_ID = 963L;
  private static final String DEFAULT_FLOW_ID = "/pay_preference";
  private static final String DEFAULT_PRODUCT_ID = "BK9TMI410T3G01IB4220";

  public PreferenceResponse getPreferenceResponse(
      final Context context, final InitPreferenceRequest initPreferenceRequest)
      throws InterruptedException, ApiException, ExecutionException {

    String preferenceId = initPreferenceRequest.getPrefId();
    if (StringUtils.isBlank(preferenceId)) {
      preferenceId = getPreferenceByShortId(context, initPreferenceRequest.getShortId());
    }

    final Preference preference =
        PreferenceService.INSTANCE.getPreference(
            context, preferenceId, initPreferenceRequest.getCallerId());

    final PublicKey publicKey =
        AuthService.INSTANCE.getPublicKey(
            context,
            preference.getCollectorId(),
            chooseClientId(preference.getClientId(), initPreferenceRequest.getClientId()));

    final String flowId = resolveFlowId(initPreferenceRequest.getFlowId(), preference);
    final String productId =
        (null != preference.getProductId()) ? preference.getProductId() : DEFAULT_PRODUCT_ID;

    final PreferenceResponse preferenceResponse =
        new PreferenceResponse(
            preferenceId, publicKey.getPublicKey(), flowId, productId, true, true);

    logInitPref(context, preferenceResponse, preference);

    return preferenceResponse;
  }

  /**
   * Devuelve informacion de la preferencia.
   *
   * @param context context
   * @param prefId id de la preferencia
   * @param callerId id del payer
   * @throws ExecutionException exexution exception
   * @throws ApiException api exception
   * @throws InterruptedException interrupted exception
   * @return Preference
   */
  public Preference getPreference(final Context context, final String prefId, final String callerId)
      throws ApiException, ExecutionException, InterruptedException {

    final CompletableFuture<Either<Preference, ApiError>> futurePreference =
        PreferenceAPI.INSTANCE.geAsyncPreference(context, prefId);

    if (!futurePreference.get().isValuePresent()) {
      final ApiError apiError = futurePreference.get().getAlternative();
      throw new ApiException(EXTERNAL_ERROR, API_CALL_PREFERENCE_FAILED, apiError.getStatus());
    }

    final Preference preference = futurePreference.get().getValue();
    validatePref(context, preference, callerId);

    return preference;
  }

  private void validatePref(
      final Context context, final Preference preference, final String callerId)
      throws ApiException {
    PREFERENCES_VALIDATOR.validate(context, preference, callerId);

    if (COLLECTORS_MELI.contains(Long.valueOf(preference.getCollectorId()))) {
      UserIdentificationResponse sensitiveUserData =
          getUserIdentificationResponse(context, callerId);
      if (null != sensitiveUserData
          && CollectionUtils.isEmpty(sensitiveUserData.getErrors())
          && null != sensitiveUserData.getData()
          && null != sensitiveUserData.getData().getUser()
          && null != sensitiveUserData.getData().getUser().getRegistrationEmail()) {
        PREFERENCES_VALIDATOR.validatePayerDifferentThatPreferenceCreator(
            context,
            sensitiveUserData.getData().getUser().getRegistrationEmail().getAddress(),
            preference);
      }
    }
  }

  private UserIdentificationResponse getUserIdentificationResponse(
      Context context, String callerId) {
    try {
      return UserIdentificationService.INSTANCE.getUserIdentification(callerId, context);
    } catch (ApiException e) {
      // Do not fail the process if API call fails
      return null;
    }
  }

  private String getPreferenceByShortId(final Context context, final String shortKey)
      throws ApiException {

    try {
      final PreferenceTidy preferenceTidy =
          PreferenceTidyAPI.INSTANCE.getPreferenceByKey(context, shortKey);
      final String[] splitLongUrl = preferenceTidy.getLongUrl().split("=");
      if (splitLongUrl.length != 2) {
        throw new ApiException(
            EXTERNAL_ERROR, INVALID_PREFERENCE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
      }
      return splitLongUrl[1];

    } catch (Exception e) {
      throw new ApiException(
          e.getMessage(), GETTING_PARAMETERS, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private String chooseClientId(final Long clientIdPreference, final String clientIdAccessToken) {

    if (!DEFAULT_CLIENT_ID.equals(clientIdPreference)) {
      return clientIdPreference.toString();
    }

    return clientIdAccessToken;
  }

  private String resolveFlowId(final String flowId, final Preference preference) {

    if (StringUtils.isNotBlank(flowId)) {
      return flowId;
    }

    AdditionalInfo additionalInfo = null;

    try {
      additionalInfo = GsonWrapper.fromJson(preference.getAdditionalInfo(), AdditionalInfo.class);

    } catch (Exception ex) {
      // Continua el flujo con el flow default.
    }

    if (null != additionalInfo
        && null != additionalInfo.getPxConfiguration()
        && null != additionalInfo.getPxConfiguration().getFlowId()) {

      return additionalInfo.getPxConfiguration().getFlowId();
    }

    return DEFAULT_FLOW_ID;
  }

  private void logInitPref(
      final Context context,
      final PreferenceResponse preferenceResponse,
      final Preference preference) {
    LOGGER.info(
        requestInLogBuilder(context.getRequestId())
            .withSource(SERVICE_NAME)
            .withStatus(HttpStatus.SC_OK)
            .withClientId(String.valueOf(preference.getClientId()))
            .withMessage(preferenceResponse.toLog(preferenceResponse))
            .build());
  }
}
