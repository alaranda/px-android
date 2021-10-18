package com.mercadolibre.dto.instructions.types;

import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.ATM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.TELECOMM;

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

public class BanamexInstruction extends Instruction {

  private static final Map<PxPaymentType, String> TITLE_PREFIXES;
  private static final Map<PxPaymentType, String> TITLE_SUFFIXES;
  private static final String SUBTITLE = "Paga con estos datos";

  private static final Map<PxPaymentType, List<String>> INFO_TEXTS;
  private static final String SECONDARY_INFO_TEXT = "También enviamos estos datos a tu email";
  private static final Map<PxPaymentType, List<String>> TERTIARY_INFO_TEXTS;

  private static final Map<PxPaymentType, List<Action>> ACTIONS;
  private static final String REFERENCE_BRANCH_OFFICE_LABEL = "Sucursal";
  private static final String REFERENCE_ACCOUNT_NUMBER_LABEL = "Número de cuenta";
  private static final String REFERENCE_LABEL = "Referencia";
  private static final String DASH_SEPARATOR = "-";
  private static final int SPLIT_NUMBER = 4;
  private static final int BRANCH_OFFICE_SPLIT_INDEX = 0;
  private static final int ACCOUNT_NUMBER_SPLIT_INDEX = 1;

  static {
    TITLE_PREFIXES = new HashMap<>();
    TITLE_PREFIXES.put(BANK_TRANSFER, "Listo, solo te falta pagar");
    TITLE_PREFIXES.put(ATM, "Listo, solo te falta depositar");
    TITLE_PREFIXES.put(TELECOMM, "Listo, solo te falta depositar");
  }

  static {
    TITLE_SUFFIXES = new HashMap<>();
    TITLE_SUFFIXES.put(BANK_TRANSFER, "en tu banca en línea de Banamex");
    TITLE_SUFFIXES.put(ATM, "en el Banamex más cercano");
    TITLE_SUFFIXES.put(TELECOMM, "en el Telecomm más cercano");
  }

  static {
    INFO_TEXTS = new HashMap<>();
    INFO_TEXTS.put(ATM, Collections.singletonList("Díctale estos códigos al cajero"));
    INFO_TEXTS.put(
        TELECOMM,
        Collections.singletonList(
            "Indicale al cajero que es un pago de servicios Banamex y te pedirá estos datos"));
  }

  static {
    TERTIARY_INFO_TEXTS = new HashMap<>();
    TERTIARY_INFO_TEXTS.put(
        TELECOMM,
        Collections.singletonList(
            "Si pagas un fin de semana o feriado, será al siguiente día hábil."));
  }

  static {
    ACTIONS = new HashMap<>();
    ACTIONS.put(
        BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca en línea")
                .tag(ActionTag.LINK)
                .url("http://www.banamex.com.mx")
                .build()));
  }

  @Override
  public boolean hasCompany() {
    return Boolean.TRUE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(
        TITLE_PREFIXES.get(mold.getPaymentType()),
        mold.getAmount(),
        TITLE_SUFFIXES.get(mold.getPaymentType()));
    this.subtitle = SUBTITLE;
    this.info = INFO_TEXTS.get(mold.getPaymentType());
    this.secondaryInfo = Collections.singletonList(SECONDARY_INFO_TEXT);
    this.tertiaryInfo = TERTIARY_INFO_TEXTS.get(mold.getPaymentType());
    this.createActions(mold);
    this.createReferences(mold);
    return this;
  }

  @Override
  public void createActions(final InstructionMold mold) {
    this.actions = ACTIONS.get(mold.getPaymentType());
  }

  @Override
  public void createReferences(final InstructionMold mold) {
    final Reference.ReferenceBuilder branchOfficeReferenceBuilder =
        Reference.builder().label(REFERENCE_BRANCH_OFFICE_LABEL);

    final Reference.ReferenceBuilder accountNumberReferenceBuilder =
        Reference.builder().label(REFERENCE_ACCOUNT_NUMBER_LABEL);

    final Reference reference =
        Reference.builder()
            .fieldValue(Collections.singletonList(mold.getTransactionId()))
            .label(REFERENCE_LABEL)
            .separator(SEPARATOR)
            .build();

    switch (mold.getPaymentType()) {
      case BANK_TRANSFER:
        this.references =
            ImmutableList.of(
                branchOfficeReferenceBuilder
                    .fieldValue(
                        Collections.singletonList(
                            mold.getCompany().split(DASH_SEPARATOR)[BRANCH_OFFICE_SPLIT_INDEX]))
                    .separator(SEPARATOR)
                    .build(),
                accountNumberReferenceBuilder
                    .fieldValue(
                        this.splitText(
                            mold.getCompany().split(DASH_SEPARATOR)[ACCOUNT_NUMBER_SPLIT_INDEX],
                            SPLIT_NUMBER))
                    .separator(SEPARATOR)
                    .build(),
                reference);
        break;
      case ATM:
        this.references =
            ImmutableList.of(
                branchOfficeReferenceBuilder
                    .fieldValue(Collections.singletonList(mold.getCompany()))
                    .separator(SEPARATOR)
                    .build(),
                accountNumberReferenceBuilder
                    .fieldValue(this.splitText(mold.getCompany(), SPLIT_NUMBER))
                    .separator(SEPARATOR)
                    .build(),
                reference);
        break;
      case TELECOMM:
        this.references =
            ImmutableList.of(
                branchOfficeReferenceBuilder
                    .fieldValue(Collections.singletonList(mold.getCompany()))
                    .separator(DASH_SEPARATOR)
                    .build(),
                accountNumberReferenceBuilder
                    .fieldValue(Collections.singletonList(mold.getCompany()))
                    .separator(DASH_SEPARATOR)
                    .build(),
                reference);
    }
  }
}
