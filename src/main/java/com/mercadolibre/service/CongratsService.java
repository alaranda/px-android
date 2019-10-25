package com.mercadolibre.service;

import com.mercadolibre.api.LoyaltyApi;
import com.mercadolibre.api.MerchAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.congrats.*;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.merch.MerchResponse;
import com.mercadolibre.dto.user_agent.OperatingSystem;
import com.mercadolibre.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.px.toolkit.utils.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.logs.LogBuilder;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.UrlDownloadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.mercadolibre.constants.DatadogMetricsNames.CONGRATS_ERROR_BUILD_CONGRATS;
import static com.mercadolibre.dto.user_agent.Version.CongratsApi.WITHOUT_LOYALTY_CONGRATS;


public enum  CongratsService {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Retorna los puntos sumados en el pago y los acmulados mas los descuentos otorgados.
     *
     * @param context  context
     * @param congratsRequest congrats request
     * @return Congrats congrats object
     */
    public Congrats getPointsAndDiscounts(final Context context, final CongratsRequest congratsRequest) {

        CompletableFuture<Either<Points, ApiError>> futureLoyalPoints = null;
        // TODO La comparacion con "null" esta por un bug donde me pasan el parametro en null y se transforma a string. Sacar validacion cuando muera esa version.
        if (StringUtils.isNotBlank(congratsRequest.getPaymentIds())
                && !congratsRequest.getPaymentIds().equalsIgnoreCase("null")
                && userAgentIsValid(congratsRequest.getUserAgent())) {
            futureLoyalPoints = LoyaltyApi.INSTANCE.getAsyncPoints(context, congratsRequest);
        }

        final CompletableFuture<Either<MerchResponse, ApiError>> futureMerchResponse = MerchAPI.INSTANCE.getAsyncCrossSellingAndDiscount(context, congratsRequest);

        Points points = null;
        Set<CrossSelling> crossSelling = null;
        Discounts discounts = null;

        final Optional<Points> optionalPoints = LoyaltyApi.INSTANCE.getPointsFromFuture(context, futureLoyalPoints);
        try {
            if (optionalPoints.isPresent()){

                final Points loyalPoints = optionalPoints.get();
                if (null != loyalPoints.getProgress() && null != loyalPoints.getAction() && null != loyalPoints.getTitle()) {
                    points = new Points.Builder(loyalPoints.getProgress(), loyalPoints.getTitle())
                            .action(loyalPoints.getAction(), congratsRequest.getPlatform(), congratsRequest.getUserAgent())
                            .build();
                }
            }

            Optional<MerchResponse> optionalMerchResponse = MerchAPI.INSTANCE.getMerchResponseFromFuture(context, futureMerchResponse);

            if (optionalMerchResponse.isPresent()){

                final MerchResponse merchResponse = optionalMerchResponse.get();
                if (null != merchResponse.getCrossSelling()){
                    crossSelling = new HashSet<>();
                    final String iconUrl = OnDemandResources.createOnDemandResoucesUrlByContent(congratsRequest, merchResponse.getCrossSelling().getContent(),context.getLocale());
                    crossSelling.add(new CrossSelling.Builder(merchResponse.getCrossSelling().getContent(), iconUrl).build());
                }

                if (null != merchResponse.getDiscounts() && !merchResponse.getDiscounts().getItems().isEmpty()){
                    final String downloadUrl = UrlDownloadUtils.buildDownloadUrl(congratsRequest.getPlatform());
                    discounts = new Discounts.Builder(context, merchResponse.getDiscounts(), congratsRequest.getPlatform(), downloadUrl).build();
                }
            }

            return new Congrats(points, discounts, crossSelling);
        } catch (Exception e) {
            DatadogUtils.metricCollector.incrementCounter(CONGRATS_ERROR_BUILD_CONGRATS);
            final LogBuilder logBuilder = new LogBuilder().withException(e);
            logger.error(logBuilder.build());
            return new Congrats();
        }
    }

    /**
     * Valida que para la version XXX de IOS no se devuelva puntos.
     *
     * @param userAgent  user agent
     * @return boolean user agent is valid
     */
    private boolean userAgentIsValid(final UserAgent userAgent) {

        if (userAgent.getOperatingSystem().getName().equals(OperatingSystem.IOS.getName()) && userAgent.getVersion().getVersionName().equals(WITHOUT_LOYALTY_CONGRATS.getVersionName())){
            return false;
        }
        return true;
    }

}