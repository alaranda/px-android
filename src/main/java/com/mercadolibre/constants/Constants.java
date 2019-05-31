package com.mercadolibre.constants;

import static com.mercadolibre.constants.ErrorMessagesConstants.CAN_NOT_INSTANTIATE_CONSTANTS_CLASS;

public final class Constants {

    public static final String SCOPE_PROD = "production";
    public static final String SCOPE_BETA = "beta";
    public static final String SCOPE_ALPHA = "alpha";
    public static final String API_CONTEXT_V1 = "v1";

    public static final String SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY = "services.connection.timeout";
    public static final String SERVICE_SOCKET_TIMEOUT_PROPERTY_KEY = "services.socket.timeout";
    public static final String SERVICE_RETRIES_PROPERTY_KEY = "services.retries";
    public static final String SERVICE_RETRY_DELAY_PROPERTY_KEY = "services.retry.delay";

    public static final String PUBLIC_KEY_URL_SCHEME = "public_key.url.scheme";
    public static final String PUBLIC_KEY_URL_HOST = "public_key.url.host";

    public static final String CLIENT_ID_PARAM = "client.id";
    public static final String CALLER_ID_PARAM = "caller.id";

    public static final String INSTALLMENTS = "installments";

    public static final String ACCESS_TOKEN = "acces_token";

    public static final String PREF_ID = "pref_id";
    public static final String SHORT_ID = "short_id";

    public static final String PUBLIC_KEY = "public_key";

    private Constants() {
        throw new AssertionError(CAN_NOT_INSTANTIATE_CONSTANTS_CLASS);
    }
}