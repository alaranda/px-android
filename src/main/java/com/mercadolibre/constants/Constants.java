package com.mercadolibre.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mercadolibre.constants.ErrorMessagesConstants.CAN_NOT_INSTANTIATE_CONSTANTS_CLASS;

public final class Constants {

    public static final String SCOPE_PROD = "production";
    public static final String SCOPE_BETA = "beta";
    public static final String SCOPE_ALPHA = "alpha";
    public static final String API_CONTEXT_V1 = "v1";
    public static final String API_CONTEXT_LOCALHOST = "localhost";
    public static final String API_CONTEXT = "api_context";
    public static final String REQUEST_ID = "request_id";

    public static final String SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY = "services.connection.timeout";
    public static final String SERVICE_SOCKET_TIMEOUT_PROPERTY_KEY = "services.socket.timeout";
    public static final String SERVICE_RETRIES_PROPERTY_KEY = "services.retries";
    public static final String SERVICE_RETRY_DELAY_PROPERTY_KEY = "services.retry.delay";

    public static final String PUBLIC_KEY_URL_SCHEME = "public_key.url.scheme";
    public static final String PUBLIC_KEY_URL_HOST = "public_key.url.host";

    public static final String USERS_URL_SCHEME = "users.url.scheme";
    public static final String USERS_URL_HOST = "users.url.host";

    public static final String CLIENT_ID_PARAM = "client.id";
    public static final String CALLER_ID_PARAM = "caller.id";
    public static final String SITE_ID_PARAM = "site.id";

    public static final String INSTALLMENTS = "installments";

    public static final String ACCESS_TOKEN = "access_token";

    public static final String PREF_ID = "pref_id";
    public static final String SHORT_ID = "short_id";

    public static final String PUBLIC_KEY = "public_key";

    public static final String FLOW_NAME_LEGACY_PAYMENTS = "legacy";
    public static final String FLOW_NAME_PAYMENTS_WHITELABEL = "paymentsWhiteLabel";
    public static final String FLOW_NAME_PAYMENTS_BLACKLABEL = "paymentsBlackLabel";

    public static final String ORDER_TYPE_NAME = "order_type";
    public static final String PRODUCT_ID = "product_id";
    public static final String MERCHANT_ORDER_TYPE_ML = "mercadolibre";
    public static final String MERCHANT_ORDER_TYPE_MP = "mercadopago";
    public static final String MERCHANT_ORDER = "merchant_order";
    public static final String ORDER = "order";
    public static final String WITHOUT_ORDER = "without_order";

    /**
     * Collectors de pago de factura de meli.
     */
    public static final List<Long> COLLECTORS_MELI = new ArrayList<Long>(Arrays.asList(99754138L, 73220027L,
            99628543L, 104328393L, 169885973L, 170120870L, 220115205L, 237674564L, 170120736L));

    private Constants() {
        throw new AssertionError(CAN_NOT_INSTANTIATE_CONSTANTS_CLASS);
    }
}