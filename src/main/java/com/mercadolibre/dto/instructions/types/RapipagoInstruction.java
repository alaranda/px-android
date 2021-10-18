package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import java.util.Collections;

public class RapipagoInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, solo te falta pagar";
  private static final String TITLE_SUFFIX =
      "efectivo en Rapipago. Recordá que solamente podrás pagar en las sucursales que permanecen abiertas.";
  private static final String SUBTITLE = "Paga con estos datos";

  private static final String INFO_TEXT = "Díctale este código al cajero";
  private static final String SECONDARY_INFO_TEXT = "También enviamos estos datos a tu email";

  private static final String REFERENCE_LABEL = "Código";
  private static final int SPLIT_NUMBER = 4;

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount(), TITLE_SUFFIX);
    this.subtitle = SUBTITLE;
    this.info = Collections.singletonList(INFO_TEXT);
    this.secondaryInfo = Collections.singletonList(SECONDARY_INFO_TEXT);
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
