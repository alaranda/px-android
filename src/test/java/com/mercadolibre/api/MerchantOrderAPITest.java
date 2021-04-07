package com.mercadolibre.api;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.RestClientTestBase;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

public class MerchantOrderAPITest extends RestClientTestBase {

  private final MerchantOrderAPI merchantOrderAPI = MerchantOrderAPI.INSTANCE;
  private static final String COLLECTOR_ID = "395662610";
  private final Context context = MockTestHelper.mockContextLibDto();

  @Test
  public void createMerchantOrder_isOk() throws IOException, ApiException {
    MockMerchantOrderAPI.createMerchantOrder(
        COLLECTOR_ID,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merchantOrders/merchantOrderResponse.json")));

    MerchantOrder merchantOrder =
        new MerchantOrder.Builder().withMarketplace("MLA").buildMerchantOrder();
    final Either<MerchantOrder, ApiError> merchantOrderResponse =
        merchantOrderAPI.createMerchantOrder(context, merchantOrder, COLLECTOR_ID);
    assertTrue(merchantOrderResponse.isValuePresent());
    assertThat(merchantOrderResponse.getValue(), notNullValue());
  }

  @Test
  public void createMerchantOrder_isFail() throws IOException, ApiException {
    MockMerchantOrderAPI.createMerchantOrder(
        COLLECTOR_ID,
        HttpStatus.SC_BAD_REQUEST,
        IOUtils.toString(
            getClass().getResourceAsStream("/merchantOrders/invalidMerchantOrderResponse.json")));
    MerchantOrder merchantOrder =
        new MerchantOrder.Builder().withMarketplace("MLA").buildMerchantOrder();
    final Either<MerchantOrder, ApiError> merchantOrderResponse =
        merchantOrderAPI.createMerchantOrder(context, merchantOrder, COLLECTOR_ID);
    ApiError error = merchantOrderResponse.getAlternative();
    assertThat(error, notNullValue());
    assertEquals(error.getStatus(), HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void createMerchantOrder_throwsException() throws IOException, ApiException {
    MockMerchantOrderAPI.createMerchantOrder(
        COLLECTOR_ID,
        HttpStatus.SC_BAD_REQUEST,
        IOUtils.toString(
            getClass().getResourceAsStream("/merchantOrders/invalidMerchantOrderResponse.json")));
    MerchantOrder merchantOrder =
        new MerchantOrder.Builder().withMarketplace("MLA").buildMerchantOrder();
    final Either<MerchantOrder, ApiError> merchantOrderResponse =
        merchantOrderAPI.createMerchantOrder(context, merchantOrder, COLLECTOR_ID);
    ApiError error = merchantOrderResponse.getAlternative();
    assertThat(error, notNullValue());
    assertEquals(error.getStatus(), HttpStatus.SC_BAD_REQUEST);
  }
}
