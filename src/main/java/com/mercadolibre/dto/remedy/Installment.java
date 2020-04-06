package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
public class Installment {

  private int installments;
  private BigDecimal totalAmount;
  private List<String> labels;
  private String recommendedMessage;
}
