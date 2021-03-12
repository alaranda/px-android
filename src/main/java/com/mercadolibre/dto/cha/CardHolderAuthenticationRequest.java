package com.mercadolibre.dto.cha;

import lombok.Getter;

@Getter
public class CardHolderAuthenticationRequest {

  private Card card;
  private String purchaseAmount;
  private Currency currency;
  private String sdkAppId;
  private String sdkEncData;
  private EphemSdk sdkEphemPubKey;
  private String sdkMaxTimeout;
  private String sdkReferenceNumber;
  private String sdkTransId;
  private String siteId;
}
