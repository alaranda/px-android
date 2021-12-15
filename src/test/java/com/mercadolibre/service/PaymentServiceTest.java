package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.MERCHANT_ORDER_TYPE_ML;
import static com.mercadolibre.constants.Constants.MERCHANT_ORDER_TYPE_MP;
import static com.mercadolibre.px.toolkit.utils.FileParserUtils.getObjectResponseFromFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.dto.payment.PaymentDataBody;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class PaymentServiceTest {

  private static final String PUBLIC_KEY_BLACKLABEL_AM =
      "TEST-d783da36-74a2-4378-85d1-76f498ca92c4";

  private static final String PREFERENCE_ORDER = "384414502-d095679d-f7d9-4653-ad71-4fb5feda3494";
  private static final String PREFERENCE_MERCHANT_ORDER =
      "105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb";
  private static final String CLIENT_ID_TEST = "000000";
  private static final String CALLER_ID_TEST = "1111111";

  public static final Context CONTEXT_ES = MockTestHelper.mockContextLibDto();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void getPaymentRequest_orderId_orderIdML()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ORDER,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/384414502-d095679d-f7d9-4653-ad71-4fb5feda3494.json")));

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/blackLabelAccountMoney.json", PaymentDataBody.class);

    final PaymentRequest paymentRequest =
        PaymentService.INSTANCE.getPaymentRequest(
            CONTEXT_ES,
            paymentDataBody,
            PUBLIC_KEY_BLACKLABEL_AM,
            CALLER_ID_TEST,
            CLIENT_ID_TEST,
            new Headers());

    assertThat(paymentRequest.getBody().getOrder().getId(), is(11111L));
    assertThat(paymentRequest.getBody().getOrder().getType(), is(MERCHANT_ORDER_TYPE_ML));
    final BigDecimal roundedApplicationFee = BigDecimal.valueOf(20.23);
    assertThat(paymentRequest.getBody().getApplicationFee(), is(roundedApplicationFee));
  }

  @Test
  public void getPaymentRequest_orderId_invalidOrderPayerId()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ORDER,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/384414502-d095679d-f7d9-4653-ad71-4fb5feda3494.json")));

    try {
      final PaymentDataBody paymentDataBody =
          getObjectResponseFromFile(
              "/paymentRequestBody/blackLabelAccountMoney.json", PaymentDataBody.class);

      final PaymentRequest paymentRequest =
          PaymentService.INSTANCE.getPaymentRequest(
              CONTEXT_ES,
              paymentDataBody,
              PUBLIC_KEY_BLACKLABEL_AM,
              "384414502",
              CLIENT_ID_TEST,
              new Headers());

      fail("Expected ApiException");
    } catch (ApiException e) {
      Assert.assertThat(e.getDescription(), is("Payer equals Collector"));
    }
  }

  @Test
  public void getPaymentRequest_merchantOrderId_merchantOrderIdMP()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_MERCHANT_ORDER,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb.json")));

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/bodyAMwithPrefMerchantOrder.json", PaymentDataBody.class);

    final PaymentRequest paymentRequest =
        PaymentService.INSTANCE.getPaymentRequest(
            CONTEXT_ES,
            paymentDataBody,
            PUBLIC_KEY_BLACKLABEL_AM,
            CALLER_ID_TEST,
            CLIENT_ID_TEST,
            new Headers());

    assertThat(paymentRequest.getBody().getOrder().getId(), is(1137532970L));
    assertThat(paymentRequest.getBody().getOrder().getType(), is(MERCHANT_ORDER_TYPE_MP));
  }

  @Test
  public void getPaymentRequest_merchantOrderIdInRequest_merchantOrderIdMP()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_MERCHANT_ORDER,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb.json")));

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile("/paymentRequestBody/bodyWithMOBody.json", PaymentDataBody.class);

    final PaymentRequest paymentRequest =
        PaymentService.INSTANCE.getPaymentRequest(
            CONTEXT_ES,
            paymentDataBody,
            PUBLIC_KEY_BLACKLABEL_AM,
            CALLER_ID_TEST,
            CLIENT_ID_TEST,
            new Headers());

    assertThat(paymentRequest.getBody().getOrder().getId(), is(123456789L));
    assertThat(paymentRequest.getBody().getOrder().getType(), is(MERCHANT_ORDER_TYPE_MP));
  }

  @Test
  public void getPaymentRequest_merchantOrderIdInRequest_merchantOrderIdMPWithPointOfInteraction()
      throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_MERCHANT_ORDER,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb.json")));

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/bodyWithPixPaymentMethod.json", PaymentDataBody.class);

    final PaymentRequest paymentRequest =
        PaymentService.INSTANCE.getPaymentRequest(
            CONTEXT_ES,
            paymentDataBody,
            PUBLIC_KEY_BLACKLABEL_AM,
            CALLER_ID_TEST,
            CLIENT_ID_TEST,
            new Headers());

    assertThat(paymentRequest.getBody().getOrder().getId(), is(123456789L));
    assertThat(paymentRequest.getBody().getOrder().getType(), is(MERCHANT_ORDER_TYPE_MP));
  }

  @Test
  public void
      getPaymentRequest_merchantOrderIdInRequestWhiteLabel_merchantOrderIdMPWithPointOfInteraction()
          throws IOException, InterruptedException, ApiException, ExecutionException {

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_MERCHANT_ORDER,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb.json")));

    final PaymentDataBody paymentDataBody =
        getObjectResponseFromFile(
            "/paymentRequestBody/bodyWithPixPaymentMethod.json", PaymentDataBody.class);

    final PaymentRequest paymentRequest =
        PaymentService.INSTANCE.getPaymentRequest(
            CONTEXT_ES,
            paymentDataBody,
            PUBLIC_KEY_BLACKLABEL_AM,
            null,
            CLIENT_ID_TEST,
            new Headers());

    assertNull(paymentRequest.getBody().getOrder());
  }
}
