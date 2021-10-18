package com.mercadolibre.dto.instructions.types;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import com.mercadolibre.dto.payment.TransactionDetails;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class OxxoInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Listo, solo te falta depositar";
  private static final String TITLE_SUFFIX = "en el OXXO más cercano";
  private static final String SUBTITLE = "Paga con estos datos";

  private static final String INFO_TEXT = "Díctale estos códigos al cajero";
  private static final String SECONDARY_INFO_TEXT = "También enviamos estos datos a tu email";

  private static final String REFERENCE_LABEL = "Codigo";
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
                .label(REFERENCE_LABEL)
                .fieldValue(this.splitText(mold.getPaymentCode(), SPLIT_NUMBER))
                .separator(SEPARATOR)
                .build());
  }

  public static class OxxoPaymentCode {
    private static final String OXXO_PAYMENT_CODE_PREFIX = "970000";

    /**
     * Transform a barcode to payment code for Oxxo.
     *
     * @param transactionDetails TransactionDetails.class
     * @return a payment code for Oxxo.
     */
    public static String getOxxoCode(final TransactionDetails transactionDetails) {
      if (null != transactionDetails) {
        final Optional<String> referenceIdOpt =
            Optional.ofNullable(transactionDetails.getPaymentMethodReferenceId());

        if (referenceIdOpt.isPresent()
            && referenceIdOpt.get().startsWith(OXXO_PAYMENT_CODE_PREFIX)) {
          return referenceIdOpt.get();
        }
        return Objects.toString((transactionDetails.getVerificationCode()), "");
      }
      return "";
    }
  }
}
