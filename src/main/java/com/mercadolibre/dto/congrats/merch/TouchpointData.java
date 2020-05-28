package com.mercadolibre.dto.congrats.merch;

public class TouchpointData<T> {

  private final String id;
  private final String type;
  private final T content;
  private final EdgeInsets additionalEdgeInsets;

  public TouchpointData(
      final String id, final String type, final T content, final EdgeInsets additionalEdgeInsets) {
    this.id = id;
    this.type = type;
    this.content = content;
    this.additionalEdgeInsets = additionalEdgeInsets;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public Object getContent() {
    return content;
  }

  public EdgeInsets getAdditionalEdgeInsets() {
    return additionalEdgeInsets;
  }

  @Override
  public String toString() {
    return "TouchPointContent{"
        + "id='"
        + id
        + '\''
        + ", type='"
        + type
        + '\''
        + ", content="
        + content
        + ", additionalEdgeInsets="
        + additionalEdgeInsets
        + '}';
  }
}
