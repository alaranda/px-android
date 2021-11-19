package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.API_CALL_PAYMENTS_FAILED;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import spark.utils.IOUtils;

public class PaymentAPITest extends RestClientTestBase {

  private final Long CALLER_ID_MLA = 243962506L;
  private final Long CLIENT_ID_MLA = 889238428771302L;
  private final Context context = MockTestHelper.mockContextLibDto();
  private final PaymentAPI paymentAPI = PaymentAPI.INSTANCE;

  @Test
  public void doPayment_bodyOk_approved() throws IOException, ApiException {
    MockPaymentAPI.doPayment(
        CALLER_ID_MLA,
        CLIENT_ID_MLA,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

    final PaymentBody body =
        GsonWrapper.fromJson(
            IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
            PaymentBody.class);

    final Either<Payment, ApiError> paymentResponse =
        paymentAPI.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
    final Payment payment = paymentResponse.getValue();

    assertThat(payment.getStatus(), is("approved"));
    assertThat(payment.getStatusDetail(), is("accredited"));
  }

  @Test
  public void doPayment_bodyError_400() throws IOException, ApiException {
    MockPaymentAPI.doPayment(
        CALLER_ID_MLA,
        CLIENT_ID_MLA,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/payment/status400.json")));
    final PaymentBody body =
        GsonWrapper.fromJson(
            IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
            PaymentBody.class);
    final Either<Payment, ApiError> paymentResponse =
        paymentAPI.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
    assertFalse(paymentResponse.isValuePresent());
    assertThat(paymentResponse.getAlternative(), notNullValue());
  }

  @Test
  public void doPayment_timeout_throwsException() throws IOException {
    try {
      MockPaymentAPI.doPaymentFail(12345L, 111L);
      final PaymentBody body =
          GsonWrapper.fromJson(
              IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
              PaymentBody.class);
      paymentAPI.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
      fail("should fail");
    } catch (ApiException e) {
      assertEquals(EXTERNAL_ERROR, e.getCode());
      assertEquals(HttpStatus.SC_BAD_GATEWAY, e.getStatusCode());
      assertEquals(API_CALL_PAYMENTS_FAILED, e.getDescription());
    }
  }

  @Test
  public void getPayment_ok()
      throws IOException, ApiException, ExecutionException, InterruptedException {
    final String paymentId = "4141386674";
    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));
    CompletableFuture<Either<Payment, ApiError>> future =
        paymentAPI.getAsyncPayment(context, paymentId);
    assertTrue(future != null && future.get().isValuePresent());
    assertEquals(Long.valueOf(paymentId), future.get().getValue().getId());
  }

  @Test
  public void getPayment_timeout_throws_exception(@Mocked MeliRestUtils restUtils)
      throws RestException {

    new Expectations() {
      {
        restUtils.newRestRequestBuilder("PaymentsRead");
        result = new RestException("mocked rest exception thrown");
      }
    };

    ApiException apiException =
        Assertions.assertThrows(
            ApiException.class, () -> paymentAPI.getAsyncPayment(context, "123"));

    MatcherAssert.assertThat(apiException.getCode(), is(EXTERNAL_ERROR));
    MatcherAssert.assertThat(apiException.getStatusCode(), is(HttpStatus.SC_BAD_GATEWAY));
    MatcherAssert.assertThat(apiException.getDescription(), is(API_CALL_PAYMENTS_FAILED));
  }
}
