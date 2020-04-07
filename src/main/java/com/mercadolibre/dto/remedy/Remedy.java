package com.mercadolibre.dto.remedy;

public enum Remedy {

  // HIGH RISK
  CC_REJECTED_HIGH_RISK("cc_rejected_high_risk"),
  REJECTED_HIGH_RISK("rejected_high_risk"),

  // SECURITY CODE
  CC_REJECTED_BAD_FILLED_SECURITY_CODE("cc_rejected_bad_filled_security_code"),

  // CALL FOR AUTH
  CC_REJECTED_CALL_FOR_AUTHORIZE("cc_rejected_call_for_authorize"),

  // BAD FILLED DATE
  CC_REJECTED_BAD_FILLED_DATE("without_remedy"),

  // INSUFFICIENT_AMOUNT
  CC_REJECTED_INSUFFICIENT_AMOUNT("without_remedy"),

  // OTHER REASON
  CC_REJECTED_OTHER_REASON("without_remedy"),

  // MAX ATTEMPTS
  CC_REJECTED_MAX_ATTEMPTS("without_remedy"),

  // BLACKLIST
  CC_REJECTED_BLACKLIST("without_remedy"),

  // INVALID INSTALLMENTS
  CC_REJECTED_INVALID_INSTALLMENTS("without_remedy"),

  // BAD FILLED CARD NUMBER
  CC_REJECTED_BAD_FILLED_CARD_NUMBER("without_remedy"),

  // BAD FILLED OTHER
  CC_REJECTED_BAD_FILLED_OTHER("without_remedy"),

  WITHOUT_REMEDY("without_remedy");

  private String id;

  Remedy(final String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public static Remedy from(final String type) {
    for (final Remedy remedy : values()) {
      if (remedy.id.equalsIgnoreCase(type)) {
        return remedy;
      }
    }
    return WITHOUT_REMEDY;
  }
}
