package com.mercadolibre.service;

import com.mercadolibre.api.LoyaltyApi;
import com.mercadolibre.api.MerchAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.congrats.*;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.merch.MerchResponse;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.Either;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public enum  CongratsService {
    INSTANCE;

    /**
     * Retorna los puntos usmados en el pago y los acmulados mas los descuentos otorgados.
     *
     * @param context  context
     * @param congratsRequest congrats request
     * @return Congrats congrats object
     * @throws ApiException   si falla el api call (status code is not 2xx)
     */
    public Congrats getPointsAndDiscounts(final Context context, final CongratsRequest congratsRequest) {

        final CompletableFuture<Either<Points, ApiError>> futureLoyalPoints = LoyaltyApi.INSTANCE.getAsyncPoints(context, congratsRequest);
        final CompletableFuture<Either<MerchResponse, ApiError>> futureMerchResponse = MerchAPI.INSTANCE.getAsyncCrossSellingAndDiscount(context, congratsRequest);

        Points points = null;
        Set<CrossSelling> crossSelling = null;
        Discounts discounts = null;

        final Optional<Points> optionalPoints = LoyaltyApi.INSTANCE.getPointsFromFuture(context, futureLoyalPoints);
        if (optionalPoints.isPresent()){
            final Points loyalPoints = optionalPoints.get();
            points = new Points.Builder(loyalPoints.getProgress(), loyalPoints.getTitle())
                    .action(loyalPoints.getAction(), congratsRequest.getPlatform())
                    .build();
        }

        Optional<MerchResponse> optionalMerchResponse = MerchAPI.INSTANCE.getMerchResponseFromFuture(context, futureMerchResponse);
        if (optionalMerchResponse.isPresent()){
            final MerchResponse merchResponse = optionalMerchResponse.get();
            if (null != merchResponse.getCrossSelling()){
                crossSelling = new HashSet<>();
                crossSelling.add(new CrossSelling.Builder(merchResponse.getCrossSelling().getContent()).build());
            }
            if (null != merchResponse.getDiscounts()){
                discounts = new Discounts.Builder(merchResponse.getDiscounts()).build();
            }
        }

        return new Congrats(points, discounts, crossSelling);
    }
}
