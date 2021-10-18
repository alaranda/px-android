package com.mercadolibre.dto.instructions.types;

import com.google.common.collect.ImmutableList;
import com.mercadolibre.dto.instructions.Action;
import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Interaction;

public class PixInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Seu código Pix de";
  private static final String TITLE_SUFFIX = "já pode ser pago";
  private static final String ACCREDITATION_MESSAGE = "O pagamento é sem taxas e aprovado na hora.";

  private static final String INTERACTION_COPY_TITLE = "<b>1.</b> Copie o código abaixo";
  private static final String INTERACTION_COPY_PASTE_TITLE =
      "<b>2.</b> Cole o código em “Pix Copia e Cola” no app que preferir para fazer o pagamento.";
  private static final String ACTION_LABEL = "Copiar código";

  @Override
  public boolean hasAccreditationMessage() {
    return Boolean.FALSE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount(), TITLE_SUFFIX);
    this.accreditationMessage = ACCREDITATION_MESSAGE;
    this.createInteractions(mold);
    return this;
  }

  @Override
  public void createInteractions(final InstructionMold mold) {
    this.interactions =
        ImmutableList.of(
            Interaction.builder()
                .title(INTERACTION_COPY_TITLE)
                .content(mold.getQrCode())
                .action(
                    Action.builder()
                        .tag(ActionTag.COPY)
                        .label(ACTION_LABEL)
                        .content(mold.getQrCode())
                        .build())
                .showMultilineContent(Boolean.FALSE)
                .build(),
            Interaction.builder()
                .title(INTERACTION_COPY_PASTE_TITLE)
                .showMultilineContent(Boolean.TRUE)
                .build());
  }
}
