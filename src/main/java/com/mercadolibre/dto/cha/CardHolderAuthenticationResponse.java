package com.mercadolibre.dto.cha;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/** CardHolderAuthenticationResponse class. */
@Getter()
public class CardHolderAuthenticationResponse {

  private String response;

  @SerializedName("threeDSServerTransID")
  private String threeDSServerTransID;

  @SerializedName("acsReferenceNumber")
  private String acsReferenceNumber;

  private String eci;

  @SerializedName("dsTransID")
  private String dsTransID;

  @SerializedName("acsTransID")
  private String acsTransID;

  @SerializedName("chaTransID")
  private String chaTransID;
}
