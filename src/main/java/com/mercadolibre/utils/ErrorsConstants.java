package com.mercadolibre.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public final class ErrorsConstants {
    private ErrorsConstants() {
        throw new AssertionError();
    }

    public static final String API_CALL_FAILED = "API call to preference failed";
    public static final String EXTERNAL_ERROR = "external_error";
    public static final String INTERNAL_ERROR = "internall_error";
    public static final String INVALID_PARAMS = "invalid Params";
    public static final String INVALID_PREFERENCE = "invalid preference";
    public static final String GETTING_PARAMETERS = "Error getting parameters";

    public static String getGeneralError(final Locale locale) {

        final ResourceBundle resourceBundle = ResourceBundle.getBundle("CustomErrors", locale);

        return resourceBundle.getString("checkout.initpreference.error.generic");
    }

    public static String getInvalidPreferenceError(final Locale locale) {

        final ResourceBundle resourceBundle = ResourceBundle.getBundle("CustomErrors", locale);

        return resourceBundle.getString("checkout.initpreference.error.invalidpreference");
    }

}
