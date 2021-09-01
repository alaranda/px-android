package com.mercadolibre.dto.remedy;

import com.google.gson.annotations.SerializedName;

public enum CardSize {
  @SerializedName("mini")
  MINI,

  @SerializedName("small")
  SMALL,

  @SerializedName("x_small")
  X_SMALL,

  @SerializedName("medium")
  MEDIUM,

  @SerializedName("large")
  LARGE
}
