package com.mercadolibre.dto.instructions.types;

import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.ATM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANK_TRANSFER;

import com.google.common.collect.ImmutableList;
import com.mercadolibre.dto.instructions.Action;
import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import com.mercadolibre.px.dto.lib.payment.PxPaymentType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerfinInstruction extends Instruction {

  private static final Map<PxPaymentType, String> TITLE_PREFIXES;
  private static final Map<PxPaymentType, String> TITLE_SUFFIXES;
  private static final String SUBTITLE = "Paga con estos datos";

  private static final Map<PxPaymentType, List<String>> INFO_TEXTS;
  private static final String SECONDARY_INFO_TEXT = "También enviamos estos datos a tu email";

  private static final Map<PxPaymentType, List<Action>> ACTIONS;
  private static final String REFERENCE_ACCOUNT_NUMBER_LABEL = "Número de cuenta";
  private static final String REFERENCE_LABEL = "Referencia";
  private static final int SPLIT_NUMBER = 4;

  static {
    TITLE_PREFIXES = new HashMap<>();
    TITLE_PREFIXES.put(BANK_TRANSFER, "Listo, solo te falta pagar");
    TITLE_PREFIXES.put(ATM, "Listo, solo te falta depositar");
  }

  static {
    TITLE_SUFFIXES = new HashMap<>();
    TITLE_SUFFIXES.put(BANK_TRANSFER, "en tu banca en línea de Santander");
    TITLE_SUFFIXES.put(ATM, "en el Santander más cercano");
  }

  static {
    ACTIONS = new HashMap<>();
    ACTIONS.put(
        BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca en línea")
                .tag(ActionTag.LINK)
                .url("http://www.santander.com.mx")
                .build()));
  }

  static {
    INFO_TEXTS = new HashMap<>();
    INFO_TEXTS.put(
        BANK_TRANSFER, Collections.singletonList("Elige pago de servicios a MercadoLibre"));
    INFO_TEXTS.put(ATM, Collections.singletonList("Díctale estos códigos al cajero"));
  }

  @Override
  public boolean hasCompany() {
    return Boolean.TRUE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    createTitle(
        TITLE_PREFIXES.get(mold.getPaymentType()),
        mold.getAmount(),
        TITLE_SUFFIXES.get(mold.getPaymentType()));
    this.subtitle = SUBTITLE;
    this.info = INFO_TEXTS.get(mold.getPaymentType());
    this.secondaryInfo = Collections.singletonList(SECONDARY_INFO_TEXT);
    this.createActions(mold);
    this.createReferences(mold);
    return this;
  }

  @Override
  public void createActions(final InstructionMold mold) {
    this.actions = ACTIONS.get(mold.getPaymentType());
  }

  @Override
  public void createReferences(InstructionMold mold) {

    final Reference reference =
        Reference.builder()
            .fieldValue(this.splitText(mold.getTransactionId(), SPLIT_NUMBER))
            .label(REFERENCE_LABEL)
            .separator(SEPARATOR)
            .build();

    switch (mold.getPaymentType()) {
      case BANK_TRANSFER:
        this.references = Collections.singletonList(reference);
        break;
      case ATM:
        this.references =
            ImmutableList.of(
                Reference.builder()
                    .label(REFERENCE_ACCOUNT_NUMBER_LABEL)
                    .fieldValue(Collections.singletonList(mold.getCompany()))
                    .separator(SEPARATOR)
                    .build(),
                reference);
    }
  }
}
