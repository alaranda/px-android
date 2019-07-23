package com.mercadolibre.service;

import com.mercadolibre.api.MerchantOrderAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.dto.payment.BasicUser;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.newrelic.api.agent.Trace;
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
    @Trace
    public MerchantOrder createMerchantOrder(final Context context, final Preference preference, final long payerId) throws ApiException {

        if (payerId == preference.getCollectorId()) {
            throw  new ApiException(ErrorsConstants.INTERNAL_ERROR, "Payer equals Collector", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        final MerchantOrder merchantOrderRequest = new MerchantOrder.Builder()
                .withPreferenceId(preference.getId())
                .withCollector(new BasicUser(preference.getCollectorId()))
                .withPayer(new BasicUser(payerId))
                .withItems(preference.getItems())
                .withMarketplace(preference.getMarketplace())
                .withExternalReference(preference.getExternalReference())
                .withNotificationUrl(preference.getNotificationUrl())
                .buildMerchantOrder();;

        final Either<MerchantOrder, ApiError> merchantOrder = MerchantOrderAPI.INSTANCE.createMerchantOrder(context,
                merchantOrderRequest, preference.getCollectorId().toString());
        if (!merchantOrder.isValuePresent()) {
            throw new ApiException(merchantOrder.getAlternative());
        }
        return merchantOrder.getValue();
    }

}
