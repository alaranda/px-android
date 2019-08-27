package com.mercadolibre.service;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.TestUtils;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;

import static com.mercadolibre.utils.ContextUtilsTestHelper.CONTEXT_ES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MerchantOrderServiceTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    @Test
    public void createMerchantOrder_merchantOrderIdMP_ordertypeMercadopago() throws ApiException, IOException {

        final Preference preference = TestUtils.getObjectResponseFromFile("/preference/105246494-3119b11d-7f4e-4371-86b6-acd4284af2bb.json", Preference.class);
        final MerchantOrder merchantOrder = MerchantOrderService.INSTANCE.createMerchantOrder(CONTEXT_ES, preference, 11111L);

        assertThat(merchantOrder.getOrderType(), is(Constants.MERCHANT_ORDER_TYPE_MP));
    }

    @Test
    public void createMerchantOrder_merchantOrderIdML_ordertypeMercadolibre() throws IOException, ApiException {

        final Preference preference = TestUtils.getObjectResponseFromFile("/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json", Preference.class);
        final MerchantOrder merchantOrder = MerchantOrderService.INSTANCE.createMerchantOrder(CONTEXT_ES, preference, 11111L);

        assertThat(merchantOrder.getOrderType(), is(Constants.MERCHANT_ORDER_TYPE_ML));
    }

}
