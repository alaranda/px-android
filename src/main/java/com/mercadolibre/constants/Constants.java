package com.mercadolibre.constants;

import static com.mercadolibre.constants.ErrorMessagesConstants.CAN_NOT_INSTANTIATE_CONSTANTS_CLASS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Constants {

  public static final String API_CONTEXT = "api_context";

  public static final String SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY =
      "services.connection.timeout";
  public static final String SERVICE_SOCKET_TIMEOUT_PROPERTY_KEY = "services.socket.timeout";
  public static final String SERVICE_RETRIES_PROPERTY_KEY = "services.retries";
  public static final String SERVICE_RETRY_DELAY_PROPERTY_KEY = "services.retry.delay";

  public static final String PUBLIC_KEY_URL_SCHEME = "public_key.url.scheme";
  public static final String PUBLIC_KEY_URL_HOST = "public_key.url.host";

  public static final String USERS_URL_SCHEME = "users.url.scheme";
  public static final String USERS_URL_HOST = "users.url.host";

  public static final String FLOW_NAME = "flow_name";
  public static final String PAYMENT_ID = "paymentId";

  public static final String INSTALLMENTS = "installments";

  public static final String PREF_ID = "pref_id";
  public static final String SHORT_ID = "short_id";

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

  public static final String API_CALL_PREFERENCE_FAILED =
      "API call to preference failed"; // todo add in toolkit
  public static final String API_CALL_PREFERENCE_TIDY_FAILED =
      "API call to preferenceTidy failed"; // TODO
  public static final String API_CALL_PAYMENTS_FAILED = "API call to payments failed"; // TODO
  public static final String API_CALL_PUBLIC_KEY_FAILED = "API call to public key failed"; // TODO
  public static final String INVALID_PARAMS = "invalid Params"; // TODO
  public static final String INVALID_PREFERENCE = "invalid preference"; // TODO
  public static final String GETTING_PARAMETERS = "Error getting parameters"; // TODO

  public static final String IFPE_MESSAGE_COLOR = "#cc000000";

  /** Collectors de pago de factura de meli. */
  public static final List<Long> COLLECTORS_MELI =
      new ArrayList<Long>(
          Arrays.asList(
              99754138L,
              73220027L,
              99628543L,
              104328393L,
              169885973L,
              170120870L,
              220115205L,
              237674564L,
              170120736L));

  private Constants() {
    throw new AssertionError(CAN_NOT_INSTANTIATE_CONSTANTS_CLASS);
  }
}
