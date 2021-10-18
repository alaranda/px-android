package com.mercadolibre.dto.instructions.types;

import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BALOTO;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BALOTO_472;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BALOTO_SER;

import com.google.common.collect.ImmutableList;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import com.mercadolibre.px.dto.lib.payment.PxPaymentType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BalotoInstruction extends Instruction {

  private static final Map<PxPaymentType, String> TITLE_PREFIXES;

  private static final String INFO_TEXT = "Díctale estos números al cajero:";
  private static final String TERTIARY_INFO_TEXT_1 =
      "también te enviamos todos los datos a tu e-mail.";
  private static final String TERTIARY_INFO_TEXT_2 =
      "Si lo abres desde tu teléfono, será más fácil dictárselo al cajero.";

  private static final String REFERENCE_LABEL_COMPANY = "Código de convenio";
  private static final String REFERENCE_LABEL_PAYMENT = "Referencia de Pago";

  static {
    TITLE_PREFIXES = new HashMap<>();
    TITLE_PREFIXES.put(BALOTO, "Listo, ahora ve a Via Baloto y paga");
    TITLE_PREFIXES.put(BALOTO_SER, "Listo, ahora ve a Soluciones en Red y paga");
    TITLE_PREFIXES.put(BALOTO_472, "Listo, ahora ve a 4-72 y paga");
  }

  @Override
  public boolean hasCompany() {
    return Boolean.TRUE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIXES.get(mold.getPaymentType()), mold.getAmount());
    this.info = Collections.singletonList(INFO_TEXT);
    this.tertiaryInfo = ImmutableList.of(TERTIARY_INFO_TEXT_1, TERTIARY_INFO_TEXT_2);
    this.createReferences(mold);
    return this;
  }

  @Override
  public void createReferences(final InstructionMold mold) {
    this.references =
        ImmutableList.of(
            Reference.builder()
                .label(REFERENCE_LABEL_COMPANY)
                .fieldValue(Collections.singletonList(mold.getCompany()))
                .build(),
            Reference.builder()
                .label(REFERENCE_LABEL_PAYMENT)
                .fieldValue(Collections.singletonList(mold.getPaymentId()))
                .build());
  }
}
