package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PIX;

import com.google.common.collect.Lists;
import com.mercadolibre.px.dto.lib.preference.PointOfInteraction;
import java.util.List;

public final class PaymentMethodsUtils {

  // POINT OF INTERACTION constants
  private static final String POINT_OF_INTERACTION_TYPE = "CHECKOUT";
  private static final String APPLICATION_DATA_NAME = "px-checkout-mobile-payments";
  private static final String APPLICATION_DATA_VERSION = "v1";

  private static final List<String> PM_WITH_POINT_OF_INTERACTION = Lists.newArrayList(PIX);

  public static PointOfInteraction getPointOfInteraction(final String paymentMethodId) {
    return PM_WITH_POINT_OF_INTERACTION.contains(paymentMethodId)
        ? createPointOfInteraction()
        : null;
  }

  private static PointOfInteraction createPointOfInteraction() {
    return new PointOfInteraction(
        POINT_OF_INTERACTION_TYPE,
        new PointOfInteraction.ApplicationData(APPLICATION_DATA_NAME, APPLICATION_DATA_VERSION));
  }
}
