package com.mercadolibre.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.installments.PaymentMethod;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.FileParserUtils;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class PaymentMethodsSearchApiTest extends RestClientTestBase {

  private PaymentMethodsSearchApi paymentMethodsSearchApi;

  @Before
  public void onSetup() {
    RequestMockHolder.clear();
    this.paymentMethodsSearchApi = new PaymentMethodsSearchApi();
  }

  @Test
  public void testGetPaymentMethodsAsync_getPaymentMethods200() throws ApiException {
    final Context context = MockTestHelper.mockContextLibDto();

    final String siteId = Site.MLB.getSiteId();
    final String marketplace = "NONE";
    final String paymentMethodId = "pix";

    MockPaymentMethodSearchAPI.getPaymentMethodsAsync(
        siteId,
        marketplace,
        paymentMethodId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile(
            "/paymentMethods/payment_methods_response_200.json"));

    final CompletableFuture<Either<PaymentMethodsSearchApi.PaymentMethodsSearchDTO, ApiError>>
        pmsFuture =
            this.paymentMethodsSearchApi.getPaymentMethodsAsync(
                context, siteId, marketplace, paymentMethodId);

    final Optional<PaymentMethodsSearchApi.PaymentMethodsSearchDTO> pmsOptional =
        this.paymentMethodsSearchApi.getPaymentMethodsFromFuture(context, pmsFuture);
    final Collection<PaymentMethod> pms = pmsOptional.get().getResults();

    assertFalse(pms.isEmpty());
  }

  @Test
  public void testGetPaymentMethodsAsync_getPaymentMethodsEmpty200() throws ApiException {
    final Context context = MockTestHelper.mockContextLibDto();

    final String siteId = Site.MLA.getSiteId();
    final String marketplace = "NONE";
    final String paymentMethodId = "pix2";

    MockPaymentMethodSearchAPI.getPaymentMethodsAsync(
        siteId,
        marketplace,
        paymentMethodId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile(
            "/paymentMethods/payment_methods_response_empty_200.json"));

    final CompletableFuture<Either<PaymentMethodsSearchApi.PaymentMethodsSearchDTO, ApiError>>
        pmsFuture =
            this.paymentMethodsSearchApi.getPaymentMethodsAsync(
                context, siteId, marketplace, paymentMethodId);

    final Optional<PaymentMethodsSearchApi.PaymentMethodsSearchDTO> pmsOptional =
        this.paymentMethodsSearchApi.getPaymentMethodsFromFuture(context, pmsFuture);
    final Collection<PaymentMethod> pms = pmsOptional.get().getResults();

    assertTrue(pms.isEmpty());
  }

  @Test
  public void testGetPaymentMethodsAsync_throwsApiException() throws ApiException {
    final Context context = MockTestHelper.mockContextLibDto();

    final String siteId = Site.MLA.getSiteId();
    final String marketplace = "NONE";
    final String paymentMethodId = "pix2";

    MockPaymentMethodSearchAPI.getPaymentMethodsAsync(
        siteId,
        marketplace,
        paymentMethodId,
        HttpStatus.SC_NOT_FOUND,
        FileParserUtils.getStringResponseFromFile(
            "/paymentMethods/payment_methods_response_404.json"));

    final CompletableFuture<Either<PaymentMethodsSearchApi.PaymentMethodsSearchDTO, ApiError>>
        pmsFuture =
            this.paymentMethodsSearchApi.getPaymentMethodsAsync(
                context, siteId, marketplace, paymentMethodId);

    final Optional<PaymentMethodsSearchApi.PaymentMethodsSearchDTO> pmsOptional =
        this.paymentMethodsSearchApi.getPaymentMethodsFromFuture(context, pmsFuture);

    assertFalse(pmsOptional.isPresent());
  }

  @Test
  public void testGetPaymentMethodsAsync_nullFuture_throwsApiException() throws ApiException {
    final Context context = MockTestHelper.mockContextLibDto();

    final Optional<PaymentMethodsSearchApi.PaymentMethodsSearchDTO> pmsOptional =
        this.paymentMethodsSearchApi.getPaymentMethodsFromFuture(context, null);

    assertFalse(pmsOptional.isPresent());
  }

  @Test
  public void testGetPaymentMethodsAsync_onServerError_throwsApiException(
      @Mocked MeliRestUtils restUtils) throws RestException {
    final Context context = MockTestHelper.mockContextLibDto();

    final String siteId = Site.MLA.getSiteId();
    final String marketplace = "NONE";
    final String paymentMethodId = "pix2";

    new Expectations() {
      {
        restUtils.newRestRequestBuilder("payment_methods_search");
        result = new RestException("mocked rest exception thrown");
      }
    };

    ApiException apiException =
        Assertions.assertThrows(
            ApiException.class,
            () ->
                this.paymentMethodsSearchApi.getPaymentMethodsAsync(
                    context, siteId, marketplace, paymentMethodId));

    assertThat(apiException.getCode(), is("external_error"));
    assertThat(apiException.getStatusCode(), is(HttpStatus.SC_GATEWAY_TIMEOUT));
    assertThat(apiException.getDescription(), is("API call to payment methods search failed"));
  }

  @Test
  public void testParseAsync_onInterruptedException_shouldThrowApiException()
      throws ExecutionException, InterruptedException {

    final Context context = MockTestHelper.mockContextLibDto();
    final CompletableFuture failedCompletable = mock(CompletableFuture.class);
    when(failedCompletable.get()).thenThrow(new InterruptedException());

    Assertions.assertThrows(
        ApiException.class,
        () -> this.paymentMethodsSearchApi.getPaymentMethodsFromFuture(context, failedCompletable));
  }
}
