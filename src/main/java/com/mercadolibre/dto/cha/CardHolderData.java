package com.mercadolibre.dto.cha;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
public class CardHolderData {

  private final String authTransaction = "01";
  @Setter private Card card;
  private final DeviceRenderOptions deviceRenderOptions = new DeviceRenderOptions();
  private final String processingMode = "dataonly";
  private final String profileId = "mobile_stp";
  @Setter private String purchaseAmount;
  @Setter private String purchaseCurrency;
  @Setter private String purchaseDate;
  @Setter private Integer purchaseExponent;
  private final String riskRouting = "0";
  private String sdkAppID;
  private String sdkEncData;
  private EphemSdk sdkEphemPubKey;
  private String sdkMaxTimeout;
  private String sdkReferenceNumber;
  private String sdkTransID;
  private final String shipIndicator = "07";
  @Setter private String siteId;

  public CardHolderData setSdkData(
      String sdkAppId,
      String sdkEncData,
      EphemSdk sdkEphemPubKey,
      String sdkMaxTimeout,
      String sdkReferenceNumber,
      String sdkTransId) {
    this.sdkAppID = sdkAppId;
    this.sdkEncData = sdkEncData;
    this.sdkEphemPubKey = sdkEphemPubKey;
    this.sdkMaxTimeout = sdkMaxTimeout;
    this.sdkReferenceNumber = sdkReferenceNumber;
    this.sdkTransID = sdkTransId;

    return this;
  }
}

final class DeviceRenderOptions {
  private final String sdkInterface = "01";
  private final List<String> sdkUiType = Arrays.asList("01", "02", "03");
}
