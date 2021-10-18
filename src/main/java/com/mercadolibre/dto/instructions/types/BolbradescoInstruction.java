package com.mercadolibre.dto.instructions.types;

import com.google.common.collect.ImmutableList;
import com.mercadolibre.dto.instructions.Action;
import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Interaction;
import com.mercadolibre.dto.payment.Barcode;
import java.util.Collections;
import java.util.Objects;

public class BolbradescoInstruction extends Instruction {

  private static final String TITLE_PREFIX = "Para concluir, você deve pagar seu boleto de";
  private static final String ACCREDITATION_MESSAGE =
      "O valor será creditado em 1 ou 2 dias úteis.";

  private static final String SECONDARY_INFO_TEXT =
      "Enviamos o boleto por e-mail para que você o tenha disponível quando precisar.";

  private static final String INTERACTION_COPY_TITLE =
      "Você pode fazer isso pelo Internet Banking com o seu código.";
  private static final String INTERACTION_COPY_ACTION_LABEL = "Copiar código";
  private static final String INTERACTION_LINK_ACTION_LABEL = "Ver boleto";
  private static final String INTERACTION_LINK_TITLE =
      "Ou imprimir o boleto para pagá-lo em uma agência bancária.";

  @Override
  public boolean hasAccreditationMessage() {
    return Boolean.FALSE;
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(TITLE_PREFIX, mold.getAmount());
    this.accreditationMessage = ACCREDITATION_MESSAGE;
    this.secondaryInfo = Collections.singletonList(SECONDARY_INFO_TEXT);
    this.createInteractions(mold);
    return this;
  }

  @Override
  public void createInteractions(final InstructionMold mold) {
    this.interactions =
        ImmutableList.of(
            Interaction.builder()
                .title(INTERACTION_COPY_TITLE)
                .content(mold.getPaymentCode())
                .action(
                    Action.builder()
                        .tag(ActionTag.COPY)
                        .label(INTERACTION_COPY_ACTION_LABEL)
                        .content(mold.getPaymentCode())
                        .build())
                .showMultilineContent(Boolean.TRUE)
                .build(),
            Interaction.builder()
                .title(INTERACTION_LINK_TITLE)
                .action(
                    Action.builder()
                        .tag(ActionTag.LINK)
                        .label(INTERACTION_LINK_ACTION_LABEL)
                        .url(mold.getActivationUri())
                        .build())
                .showMultilineContent(Boolean.TRUE)
                .build());
  }

  public static class BolbradescoPaymentCode {
    private static final int BOLBRADESCO_MIN_LENGTH = 44;
    private static final char BOLBRADESCO_SPACE_SEPARATOR = ' ';
    private static final char DOT_SEPARATOR = '.';

    /**
     * Transform a barcode to payment code for Bolbradesco.
     *
     * @param barcode Barcode.class
     * @return a payment code for Bolbradesco
     */
    public static String getBolbradescoCode(final Barcode barcode) {
      if (null != barcode) {
        final String content = Objects.toString(barcode.getContent(), "");

        if (content.length() >= BOLBRADESCO_MIN_LENGTH) {
          return getCodePart(content.substring(0, 4) + content.substring(19, 24))
              + getCodePart(content.substring(24, 34))
              + getCodePart(content.substring(34, 44))
              + content.charAt(4)
              + BOLBRADESCO_SPACE_SEPARATOR
              + content.substring(5, 19);
        }
      }
      return "";
    }

    /**
     * Get a part of payment code for Bolbradesco.
     *
     * @param split String
     * @return part of payment code
     */
    private static String getCodePart(final String split) {
      return new StringBuilder(split)
          .append(calcModule10(split))
          .insert(5, DOT_SEPARATOR)
          .append(BOLBRADESCO_SPACE_SEPARATOR)
          .toString();
    }

    /**
     * Calculate the part of Bolbradesco code.
     *
     * @param split String
     * @return a part of Bolbradesco code calculated
     */
    private static String calcModule10(final String split) {
      final int sum = calcModule10Iteration(split, 0, 2, split.length() - 1);

      final int digit = 10 - (sum % 10);

      return digit == 10 ? "0" : String.valueOf(digit);
    }

    /**
     * Calculate the module 10 of Bolbradesco code with recursive method.
     *
     * @param split String
     * @param sum int
     * @param pound int
     * @param index int
     * @return int
     */
    private static int calcModule10Iteration(
        final String split, final int sum, final int pound, final int index) {
      if (index >= 0) {
        int multiply = Integer.parseInt(split.substring(index, index + 1)) * pound;

        if (multiply >= 10) {
          multiply -= 9;
        }

        return calcModule10Iteration(split, sum + multiply, pound == 2 ? 1 : 2, index - 1);
      }
      return sum;
    }
  }
}
