package com.mercadolibre.dto.payment;

import lombok.Getter;

@Getter
public class TransactionData {

  private BankInfo bankInfo;
  private String qrCodeBase64;
  private String qrCode;
}
