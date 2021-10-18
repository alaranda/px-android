package com.mercadolibre.dto.instructions;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionTag {
  @SerializedName("copy")
  COPY,

  @SerializedName("link")
  LINK,

  @SerializedName("print")
  PRINT,

  @SerializedName("expirable_button")
  EXPIRABLE_BUTTON
}
