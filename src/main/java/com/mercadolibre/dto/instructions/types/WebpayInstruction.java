package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Action;
import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import java.util.Collections;

public class WebpayInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, ahora paga";
  private static final String TITLE_SUFFIX = "en WebPay";
  private static final String SUBTITLE = "Paga con estos datos";

  private static final String INFO_TEXT =
      "Ten a mano tu tarjeta de coordenadas o generador de claves.";

  private static final String ACTION_EXPIRABLE_BUTTON_LABEL = "Pagar";

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount(), TITLE_SUFFIX);
    this.subtitle = SUBTITLE;
    this.info = Collections.singletonList(INFO_TEXT);
    this.createActions(mold);
    return this;
  }

  @Override
  public void createActions(final InstructionMold mold) {
    this.actions =
        Collections.singletonList(
            Action.builder()
                .label(ACTION_EXPIRABLE_BUTTON_LABEL)
                .tag(ActionTag.EXPIRABLE_BUTTON)
                .url(mold.getActivationUri())
                .build());
  }
}
