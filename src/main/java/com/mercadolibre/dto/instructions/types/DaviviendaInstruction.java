package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import java.util.Collections;

public class DaviviendaInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, ahora ve y paga";
  private static final String TITLE_SUFFIX = "en Davivienda";
  private static final String SUBTITLE = "Paga con estos datos";
  private static final String ACCREDITATION_MESSAGE = "El pago se acreditará en 1 día hábil.";
  private static final String INFO_TEXT =
      "Imprime el cupón que te enviamos por e-mail, y acércate a cualquier sucursal";

  @Override
  public boolean hasAccreditationMessage() {
    return Boolean.FALSE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount(), TITLE_SUFFIX);
    this.subtitle = SUBTITLE;
    this.accreditationMessage = ACCREDITATION_MESSAGE;
    this.info = Collections.singletonList(INFO_TEXT);
    return this;
  }
}
