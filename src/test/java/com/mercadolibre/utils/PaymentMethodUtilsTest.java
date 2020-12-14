package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.utils.FileParserUtils.getObjectResponseFromFile;
import static org.junit.Assert.assertEquals;

import com.mercadolibre.dto.payment.PaymentDataBody;
import com.mercadolibre.px.dto.lib.preference.Preference;
import org.junit.Test;

public class PaymentMethodUtilsTest {

  @Test
  public void testGetPaymentMethodId_withoutInternalMetadata() {
    final Preference preference =
        getObjectResponseFromFile("/preference/preferenceWithOnlyPayer.json", Preference.class);

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/blackLabelAccountMoney.json", PaymentDataBody.class);

    String paymentMethodId =
        PaymentMethodUtils.getPaymentMethodId(paymentDataBody.getPaymentData().get(0), preference);
    assertEquals("account_money", paymentMethodId);
  }

  @Test
  public void testGetPaymentMethodId_withoutType() {
    final Preference preference =
        getObjectResponseFromFile(
            "/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json", Preference.class);

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/blackLabelAccountMoney.json", PaymentDataBody.class);

    String paymentMethodId =
        PaymentMethodUtils.getPaymentMethodId(paymentDataBody.getPaymentData().get(0), preference);
    assertEquals("account_money", paymentMethodId);
  }

  @Test
  public void testGetPaymentMethodId_pixAM() {
    final Preference preference =
        getObjectResponseFromFile(
            "/preference/105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb.json", Preference.class);

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/blackLabelAccountMoney.json", PaymentDataBody.class);

    String paymentMethodId =
        PaymentMethodUtils.getPaymentMethodId(paymentDataBody.getPaymentData().get(0), preference);
    assertEquals("pix_am", paymentMethodId);
  }
}
