package com.mercadolibre.dto.instructions.types;

import com.google.common.collect.ImmutableList;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import java.util.Collections;

public class PecInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Agora basta ir a uma lotérica e pagar";
  private static final String SUBTITLE = "Tenha os seguintes dados à mão:";
  private static final String ACCREDITATION_MESSAGE =
      "O dinheiro é creditado em menos de uma hora.";

  private static final String SECONDARY_INFO_TEXT =
      "Enviamos o boleto por e-mail para que você o tenha disponível quando precisar.";

  private static final String REFERENCE_COMPANY_LABEL = "Código do convênio com o Mercado Pago";
  private static final String REFERENCE_PAYER_LABEL_PREFIX = "Seu ";
  private static final int SPLIT_NUMBER = 4;

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
    super.createTitle(TITLE_PREFIX, mold.getAmount());
    this.subtitle = SUBTITLE;
    this.accreditationMessage = ACCREDITATION_MESSAGE;
    this.secondaryInfo = Collections.singletonList(SECONDARY_INFO_TEXT);
    this.createReferences(mold);
    return this;
  }

  @Override
  public void createReferences(final InstructionMold mold) {
    this.references =
        ImmutableList.of(
            Reference.builder()
                .fieldValue(this.splitText(mold.getCompany(), SPLIT_NUMBER))
                .label(REFERENCE_COMPANY_LABEL)
                .separator(SEPARATOR)
                .build(),
            Reference.builder()
                .fieldValue(Collections.singletonList(mold.getPayerIdentificationNumber()))
                .label(REFERENCE_PAYER_LABEL_PREFIX + mold.getPayerIdentificationType())
                .build());
  }
}
