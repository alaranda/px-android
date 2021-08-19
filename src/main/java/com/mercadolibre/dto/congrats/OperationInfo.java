package com.mercadolibre.dto.congrats;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationInfo {

  private String hierarchy;
  private String type;
  private String body;

  public static final class OperationInfoBuilder {
    private String hierarchy;
    private String type;

    public OperationInfoBuilder type(OperationInfoType type) {
      this.type = type.getValue();
      return this;
    }

    public OperationInfoBuilder hierarchy(OperationInfoHierarchy hierarchy) {
      this.hierarchy = hierarchy.getValue();
      return this;
    }
  }
}
