package com.mercadolibre.dto.instructions;

import com.mercadolibre.px.dto.lib.payment.PxPaymentType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InstructionMold {

  private final PxPaymentType paymentType;
  private final String amount;
  private final String accreditationMessage;
  private final String company;
  private final String paymentCode;
  private final String activationUri;
  private final String transactionId;
  private final String paymentId;
  private final String payerIdentificationNumber;
  private final String payerIdentificationType;
  private final String qrCode;
}
