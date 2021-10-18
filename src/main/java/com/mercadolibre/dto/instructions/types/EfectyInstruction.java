package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import java.util.Collections;

public class EfectyInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, ahora ve a Efecty y paga";
  private static final String SUBTITLE = "Paga con estos datos";
  private static final String ACCREDITATION_MESSAGE = "El pago se acreditará al instante.";
  private static final String INFO_TEXT = "Díctale los datos al cajero y listo.";

  private static final int SPLIT_NUMBER = 5;
  private static final String REFERENCE_LABEL = "Dile al cajero estos números:";

  @Override
  public boolean hasAccreditationMessage() {
    return Boolean.FALSE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount());
    this.subtitle = SUBTITLE;
    this.accreditationMessage = ACCREDITATION_MESSAGE;
    this.info = Collections.singletonList(INFO_TEXT);
    this.createReferences(mold);
    return this;
  }

  @Override
  public void createReferences(final InstructionMold mold) {
    this.references =
        Collections.singletonList(
            Reference.builder()
                .fieldValue(this.splitText(mold.getPaymentId(), SPLIT_NUMBER))
                .label(REFERENCE_LABEL)
                .separator(SEPARATOR)
                .build());
  }
}
