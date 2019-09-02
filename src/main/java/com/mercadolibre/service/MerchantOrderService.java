package com.mercadolibre.service;

import com.mercadolibre.api.MerchantOrderAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.dto.payment.BasicUser;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.utils.datadog.DatadogTransactionsMetrics;
import org.apache.http.HttpStatus;

public enum MerchantOrderService {

    INSTANCE;

    /**
     * Hace el API call a la API de Merchant Order usando la preferencia y el payerId para obtener la merchant order id.
     * @param context       context object
     * @param preference     preference
     * @param payerId     payer id
     * @return el objeto Merchant Order
     * @throws ApiException si falla el api call (status code is not 2xx)
     */
    public MerchantOrder createMerchantOrder(final Context context, final Preference preference, final long payerId) throws ApiException {

        if (payerId == preference.getCollectorId()) {
            throw  new ApiException(ErrorsConstants.INTERNAL_ERROR, "Payer equals Collector", HttpStatus.SC_BAD_REQUEST);
        }

        if (null != preference.getMerchantOrderId()){
            DatadogTransactionsMetrics.addOrderTypePayment("merchant_order");
            return new MerchantOrder.Builder().withOrderId(preference.getMerchantOrderId()).withOrderType(Constants.MERCHANT_ORDER_TYPE_MP).buildMerchantOrder();
        }

        if (null != preference.getOrderId()){
            DatadogTransactionsMetrics.addOrderTypePayment("order");
            return new MerchantOrder.Builder().withOrderId(preference.getOrderId()).withOrderType(Constants.MERCHANT_ORDER_TYPE_ML).buildMerchantOrder();
        }

        final MerchantOrder merchantOrderRequest = new MerchantOrder.Builder()
                .withPreferenceId(preference.getId())
                .withCollector(new BasicUser(preference.getCollectorId()))
                .withPayer(new BasicUser(payerId))
                .withItems(preference.getItems())
                .withMarketplace(preference.getMarketplace())
                .withExternalReference(preference.getExternalReference())
                .withNotificationUrl(preference.getNotificationUrl())
                .buildMerchantOrder();

        final Either<MerchantOrder, ApiError> merchantOrder = MerchantOrderAPI.INSTANCE.createMerchantOrder(context,
                merchantOrderRequest, preference.getCollectorId().toString());
        if (!merchantOrder.isValuePresent()) {
            throw new ApiException(merchantOrder.getAlternative());
        }
        DatadogTransactionsMetrics.addOrderTypePayment("new_merchant_order");
        return merchantOrder.getValue();
    }

}
