package com.mercadolibre.dto.remedy;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PayerPaymentMethodRejected {

  private final String customOptionId;
  private final String paymentMethodId;
  private final String paymentTypeId;
  private final String escStatus;
  private final boolean esc;

  @Schema(name = "issuer_name")
  private final String issuerName;

  private final String lastFourDigit;
  private final String securityCodeLocation;
  private final int securityCodeLength;
  private final int installments;

  @Schema(hidden = true)
  @Setter
  private BigDecimal totalAmount;

  private final String bin;
}
