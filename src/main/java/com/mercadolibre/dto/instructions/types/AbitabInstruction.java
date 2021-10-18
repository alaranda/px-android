package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import java.util.Collections;

public class AbitabInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, ahora ve a un Abitab y paga";
  private static final String SUBTITLE = "Paga con estos datos";

  private static final String INFO_TEXT =
      "Dile al cajero que pagarás a Mercado Pago y díctale tu CI:";
  private static final String SECONDARY_INFO_TEXT =
      "Te enviamos los datos por e-mail para que los tengas a mano.";

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount());
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
                .fieldValue(Collections.singletonList(mold.getPayerIdentificationNumber()))
                .build());
  }
}
