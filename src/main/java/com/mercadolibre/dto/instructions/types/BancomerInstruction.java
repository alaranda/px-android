package com.mercadolibre.dto.instructions.types;

import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.ATM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANORTE;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.HSBC;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.OTROS;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.SEVEN_ELEVEN;

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

public class BancomerInstruction extends Instruction {

  private static final Map<PxPaymentType, String> TITLE_PREFIXES;
  private static final Map<PxPaymentType, String> TITLE_SUFFIXES;
  private static final Map<PxPaymentType, String> SUBTITLES;

  private static final Map<PxPaymentType, List<String>> INFO_TEXTS;
  private static final Map<PxPaymentType, List<String>> SECONDARY_INFO_TEXTS;
  private static final Map<PxPaymentType, List<String>> TERTIARY_INFO_TEXTS;

  private static final Map<PxPaymentType, List<Action>> ACTIONS;
  private static final String REFERENCE_CIE_LABEL = "Número de convenio CIE";
  private static final String REFERENCE_LABEL = "Referencia";
  private static final int SPLIT_NUMBER = 4;

  static {
    TITLE_PREFIXES = new HashMap<>();
    TITLE_PREFIXES.put(SEVEN_ELEVEN, "Listo, solo te falta depositar");
    TITLE_PREFIXES.put(BANK_TRANSFER, "Listo, solo te falta pagar");
    TITLE_PREFIXES.put(ATM, "Listo, solo te falta depositar");
    TITLE_PREFIXES.put(BANORTE, "Paga");
    TITLE_PREFIXES.put(HSBC, "Paga");
    TITLE_PREFIXES.put(OTROS, "Paga");
  }

  static {
    TITLE_SUFFIXES = new HashMap<>();
    TITLE_SUFFIXES.put(SEVEN_ELEVEN, "en el 7-Eleven más cercano");
    TITLE_SUFFIXES.put(BANK_TRANSFER, "en tu banca en línea de BBVA Bancomer");
    TITLE_SUFFIXES.put(ATM, "en el BBVA Bancomer más cercano");
    TITLE_SUFFIXES.put(BANORTE, "desde tu banca en línea de Banorte");
    TITLE_SUFFIXES.put(HSBC, "desde tu banca en línea de HSBC");
    TITLE_SUFFIXES.put(OTROS, "desde tu banca en línea");
  }

  static {
    SUBTITLES = new HashMap<>();
    SUBTITLES.put(SEVEN_ELEVEN, "Paga con estos datos");
    SUBTITLES.put(BANK_TRANSFER, "Paga con estos datos");
    SUBTITLES.put(ATM, "Paga con estos datos");
  }

  static {
    INFO_TEXTS = new HashMap<>();
    INFO_TEXTS.put(
        SEVEN_ELEVEN,
        Collections.singletonList(
            "Indicale al cajero que es un pago de servicios BBVA Bancomer y te pedirá estos datos"));
    INFO_TEXTS.put(
        BANK_TRANSFER, Collections.singletonList("Elige pago de servicios a MercadoLibre"));
    INFO_TEXTS.put(ATM, Collections.singletonList("Díctale estos códigos al cajero"));
  }

  static {
    SECONDARY_INFO_TEXTS = new HashMap<>();
    SECONDARY_INFO_TEXTS.put(
        SEVEN_ELEVEN, Collections.singletonList("También enviamos estos datos a tu email"));
    SECONDARY_INFO_TEXTS.put(
        BANK_TRANSFER, Collections.singletonList("También enviamos estos datos a tu email"));
    SECONDARY_INFO_TEXTS.put(
        ATM, Collections.singletonList("También enviamos estos datos a tu email"));
    SECONDARY_INFO_TEXTS.put(
        BANORTE,
        ImmutableList.of(
            "¿Prefieres transferir desde tu computadora o tablet?",
            "Te enviamos un e-mail, para que puedas hacerlo desde tu correo."));
    SECONDARY_INFO_TEXTS.put(
        HSBC,
        ImmutableList.of(
            "¿Prefieres transferir desde tu computadora o tablet?",
            "Te enviamos un e-mail, para que puedas hacerlo desde tu correo."));
    SECONDARY_INFO_TEXTS.put(
        OTROS,
        ImmutableList.of(
            "¿Prefieres transferir desde tu computadora o tablet?",
            "Te enviamos un e-mail, para que puedas hacerlo desde tu correo."));
  }

  static {
    TERTIARY_INFO_TEXTS = new HashMap<>();
    TERTIARY_INFO_TEXTS.put(
        ATM,
        ImmutableList.of(
            "Si hay fila en BBVA Bancomer, no pierdas tiempo.",
            "Paga en el 7-Eleven más cercano con estos mismos datos."));
  }

  static {
    ACTIONS = new HashMap<>();
    ACTIONS.put(
        BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca en línea")
                .tag(ActionTag.LINK)
                .url("http://www.bancomer.com.mx")
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
    this.subtitle = SUBTITLES.get(mold.getPaymentType());
    this.info = INFO_TEXTS.get(mold.getPaymentType());
    this.secondaryInfo = SECONDARY_INFO_TEXTS.get(mold.getPaymentType());
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
    switch (mold.getPaymentType()) {
      case SEVEN_ELEVEN:
      case BANK_TRANSFER:
      case ATM:
        this.references =
            ImmutableList.of(
                Reference.builder()
                    .label(REFERENCE_CIE_LABEL)
                    .fieldValue(this.splitText(mold.getCompany(), SPLIT_NUMBER))
                    .separator(SEPARATOR)
                    .build(),
                Reference.builder()
                    .fieldValue(this.splitText(mold.getTransactionId(), SPLIT_NUMBER))
                    .label(REFERENCE_LABEL)
                    .separator(SEPARATOR)
                    .build());
    }
  }
}
