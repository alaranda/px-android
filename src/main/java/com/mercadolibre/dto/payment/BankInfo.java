package com.mercadolibre.dto.payment;

import lombok.Getter;

@Getter
public class BankInfo {

  private Account payer;
  private Account collector;
}
