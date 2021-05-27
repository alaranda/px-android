package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PIX;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.VISA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.mercadolibre.px.dto.lib.preference.PointOfInteraction;
import org.junit.Test;

public class PaymentMethodsUtilsTest {

  @Test
  public void testPointOfInteraction_get() {

    final PointOfInteraction pointOfInteraction = PaymentMethodsUtils.getPointOfInteraction(PIX);

    assertNotNull(pointOfInteraction);
    assertNotNull(pointOfInteraction.getType());
    assertNotNull(pointOfInteraction.getApplicationData());
  }

  @Test
  public void testPointOfInteraction_null() {

    final PointOfInteraction pointOfInteraction = PaymentMethodsUtils.getPointOfInteraction(VISA);

    assertNull(pointOfInteraction);
  }
}
