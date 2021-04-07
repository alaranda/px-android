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
  CC_REJECTED_BAD_FILLED_DATE("cc_rejected_bad_filled_date"),

  // INSUFFICIENT_AMOUNT
  CC_REJECTED_INSUFFICIENT_AMOUNT("cc_rejected_insufficient_amount"),

  // OTHER REASON
  CC_REJECTED_OTHER_REASON("cc_rejected_other_reason"),

  // MAX ATTEMPTS
  CC_REJECTED_MAX_ATTEMPTS("cc_rejected_max_attemps"),

  // BLACKLIST
  CC_REJECTED_BLACKLIST("cc_rejected_blacklist"),

  // INVALID INSTALLMENTS
  CC_REJECTED_INVALID_INSTALLMENTS("cc_rejected_invalid_installments"),

  // BAD FILLED CARD NUMBER
  CC_REJECTED_BAD_FILLED_CARD_NUMBER("cc_rejected_bad_filled_card_number"),

  // BAD FILLED OTHER
  CC_REJECTED_BAD_FILLED_OTHER("cc_rejected_bad_filled_other"),

  // REGULATIONS
  REJECTED_BY_REGULATIONS("rejected_by_regulations"),

  // BANK
  REJECTED_BY_BANK("rejected_by_bank"),
  REJECTED_BANK_ERROR("rejected_bank_error"),

  // CARD DISABLED
  REJECTED_CARD_DISABLED("cc_rejected_card_disabled"),

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
