package com.mercadolibre.utils;

import static com.mercadolibre.constants.Constants.PIX_PAYMENT_METHOD_ID;
import static com.mercadolibre.constants.Constants.PIX_TYPE_PREFERENCE;
import static com.mercadolibre.constants.Constants.PREFERENCE_INTERNAL_METADATA;

import com.mercadolibre.dto.payment.PaymentData;
import com.mercadolibre.px.dto.lib.preference.Preference;

public class PaymentMethodUtils {

  public static String getPaymentMethodId(
      final PaymentData paymentData, final Preference preference) {

    if (null != preference.getInternalMetadata()
        && preference.getInternalMetadata().containsKey(PREFERENCE_INTERNAL_METADATA)) {

      final String cowType =
          preference.getInternalMetadata().get(PREFERENCE_INTERNAL_METADATA).toString();

      if (cowType.equalsIgnoreCase(PIX_TYPE_PREFERENCE)) {
        return PIX_PAYMENT_METHOD_ID;
      }
    }

    return paymentData.getPaymentMethod().getId();
  }
}
