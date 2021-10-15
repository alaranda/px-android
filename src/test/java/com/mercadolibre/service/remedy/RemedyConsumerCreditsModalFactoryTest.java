package com.mercadolibre.service.remedy;

import static org.junit.Assert.*;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.modal.ModalAction;
import java.util.Locale;
import org.junit.Test;

public class RemedyConsumerCreditsModalFactoryTest {

  private static final Locale locale = new Locale("es");

  @Test
  public void testBuild_paymentMethodIdConsumerCredits() {
    ModalAction modalAction = RemedyConsumerCreditsModalFactory.INSTANCE.build(locale);

    assertNotNull(modalAction);

    assertEquals("Recuerda que usarás Mercado Crédito", modalAction.getTitle().getMessage());
    assertEquals(Constants.WEIGHT_SEMI_BOLD, modalAction.getTitle().getWeight());
    assertEquals(
        Constants.CONSUMER_CREDITS_MODAL_TEXT_COLOR, modalAction.getTitle().getTextColor());
    assertNull(modalAction.getTitle().getBackgroundColor());

    assertEquals(
        "Las cuotas tienen un valor fijo y podrás pagarlas desde tu cuenta de Mercado Pago.",
        modalAction.getDescription().getMessage());
    assertEquals(Constants.WEIGHT_REGULAR, modalAction.getDescription().getWeight());
    assertEquals(
        Constants.CONSUMER_CREDITS_MODAL_TEXT_COLOR, modalAction.getDescription().getTextColor());
    assertNull(modalAction.getDescription().getBackgroundColor());

    assertEquals("Confirmar pago", modalAction.getMainButton().getLabel());
    assertEquals(Constants.ACTION_PAY, modalAction.getMainButton().getAction());
    assertEquals(Constants.BUTTON_LOUD, modalAction.getMainButton().getType());

    assertEquals("Pagar de otra forma", modalAction.getSecondaryButton().getLabel());
    assertEquals(Constants.ACTION_CHANGE_PM, modalAction.getSecondaryButton().getAction());
    assertEquals(Constants.BUTTON_QUIET, modalAction.getSecondaryButton().getType());
  }
}
