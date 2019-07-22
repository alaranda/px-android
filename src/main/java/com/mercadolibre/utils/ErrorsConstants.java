package com.mercadolibre.utils;

import java.util.Locale;

import static com.mercadolibre.utils.Locale.getLocale;

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
    public static final String GENERAL_ERROR_ES = "En este momento no podemos procesar tu pago.";
    public static final String GENERAL_ERROR_PT = "Não podemos processar o seu pagamento no momento.";

    public static String getGeneralErrorByLanguage(final String languge) {

        final Locale locale = getLocale(languge);
        if (locale.getLanguage() == null) {
            return GENERAL_ERROR_ES;
        }
        switch (locale.getLanguage().toLowerCase()){
            case "es":
                return GENERAL_ERROR_ES;
            case "pt":
                return GENERAL_ERROR_PT;
            default:
                return GENERAL_ERROR_ES;
        }
    }
}
