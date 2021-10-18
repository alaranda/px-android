package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Action;
import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import java.util.Collections;

public class PseInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, ahora haz una transferencia de";
  private static final String TITLE_INFIX = "desde";
  private static final String SUBTITLE = "Paga con estos datos";
  private static final String ACCREDITATION_MESSAGE = "El pago se acreditará al instante.";

  private static final String SECONDARY_INFO_TEXT = "Tienes 20 minutos para hacerlo.";

  private static final String ACTION_LABEL = "Transferir";

  @Override
  public boolean hasAccreditationMessage() {
    return Boolean.FALSE;
  }

  @Override
  public boolean hasCompany() {
    return Boolean.TRUE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount(), TITLE_INFIX, mold.getCompany());
    this.subtitle = SUBTITLE;
    this.accreditationMessage = ACCREDITATION_MESSAGE;
    this.secondaryInfo = Collections.singletonList(SECONDARY_INFO_TEXT);
    this.createActions(mold);
    return this;
  }

  @Override
  public void createActions(final InstructionMold mold) {
    this.actions =
        Collections.singletonList(
            Action.builder()
                .label(ACTION_LABEL)
                .tag(ActionTag.LINK)
                .url(mold.getActivationUri())
                .build());
  }
}
