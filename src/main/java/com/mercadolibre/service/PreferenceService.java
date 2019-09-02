package com.mercadolibre.service;

import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.api.PreferenceTidyAPI;
import com.mercadolibre.api.UserAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.User;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.preference.PreferenceTidy;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.validators.PreferencesValidator;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.utils.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.Constants.COLLECTORS_MELI;

public enum PreferenceService {
    INSTANCE;

    private long DEFAULT_CLIENT_ID = 963L;

    /**
     * Devuelve informacion de la preferencia.
     *
     * @param context  context
     * @param prefId id de la preferencia
     * @param callerId id del payer
     * @return Preference
     * @throws ApiException   si falla el api call (status code is not 2xx)
     */
    public Preference getPreference(final Context context, final String prefId, final Long callerId)
            throws ApiException, ExecutionException, InterruptedException {

        final CompletableFuture<Either<Preference, ApiError>> futurePreference =
                PreferenceAPI.INSTANCE.geAsynctPreference(context, prefId);

        if (!futurePreference.get().isValuePresent()) {
            final ApiError apiError = futurePreference.get().getAlternative();
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, ErrorsConstants.API_CALL_FAILED, apiError.getStatus());
        }

        final Preference preference = futurePreference.get().getValue();
        validatePref(context, preference, callerId);

        return preference;
    }

    private void validatePref(final Context context, final Preference preference, final long callerId) throws ValidationException, ApiException {

        final PreferencesValidator validator = new PreferencesValidator();
        validator.validate(context, preference, callerId);

        if (COLLECTORS_MELI.contains(preference.getCollectorId())) {
            final User user = UserAPI.INSTANCE.getById(context, callerId);
            validator.isDifferent(context, user.getEmail(), preference.getPayer().getEmail());
        }
    }

    /**
     * Extrae del request un prefId o una shortKey, si es una shortKey hace un apiCall a tidy y devuelve un prefId.
     *
     * @param context  context
     * @param request request de spark
     * @return String preferenceId
     * @throws ApiException   si falla el api call (status code is not 2xx)
     */
    public String extractParamPrefId(final Context context, final Request request) throws ApiException {

        if (!StringUtils.isBlank(request.queryParams(Constants.SHORT_ID))){
            return isShortKey(context, request.queryParams(Constants.SHORT_ID));
        } else if (request.queryParams(Constants.PREF_ID) != null){
            return request.queryParams(Constants.PREF_ID);
        }
        throw new ApiException(ErrorsConstants.INVALID_PARAMS, ErrorsConstants.GETTING_PARAMETERS, HttpStatus.SC_BAD_REQUEST);
    }

    private String isShortKey(final Context context, final String shortKey) throws ApiException {

        try {
            final PreferenceTidy preferenceTidy = PreferenceTidyAPI.INSTANCE.getPreferenceByKey(context, shortKey);
            final String[] splitLongUrl = preferenceTidy.getLongUrl().split("=");
            if (splitLongUrl.length != 2) {
                throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, ErrorsConstants.INVALID_PREFERENCE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            return splitLongUrl[1];

        } catch (Exception e) {
            throw new ApiException(e.getMessage(), ErrorsConstants.GETTING_PARAMETERS, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Intenta obtener el clientId de la pref, si viene el default setea uno nuestro con el site del AT.
     *
     * @param clientIdPreference  client id de la pref
     * @param clientIdAccessToken client id del access token
     * @return Long clientId
     */
    public Long getClientId(final long clientIdPreference, final long clientIdAccessToken) {
        return DEFAULT_CLIENT_ID == (clientIdPreference) ? clientIdAccessToken : clientIdPreference;
    }

}
