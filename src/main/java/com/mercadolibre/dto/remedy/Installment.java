package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class Installment {

  private int installments;
  private BigDecimal totalAmount;
}
