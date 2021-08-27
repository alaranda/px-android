package com.mercadolibre.service.remedy.order;

import java.util.HashMap;
import java.util.Map;

public class PaymentMethodsRejectedTypes {

  public static final String ACCOUNT_MONEY = "account_money";
  public static final String DIGITAL_CURRENCY = "digital_currency";
  public static final String CREDIT_CARD = "credit_card";
  public static final String DEBIT_CARD = "debit_card";

  private static final Map<String, SuggestionCriteriaInterface> mapSuggestionCriteria =
      new HashMap<String, SuggestionCriteriaInterface>() {
        {
          put(ACCOUNT_MONEY, new AccountMoneyRejected());
          put(DIGITAL_CURRENCY, new DigitalCurrencyRejected());
          put(DEBIT_CARD, new DebitCardRejected());
          put(CREDIT_CARD, new CreditCardRejected());
        }
      };

  public SuggestionCriteriaInterface getSuggestionOrderCriteria(
      final String paymentMethodRejectedType) {
    return mapSuggestionCriteria.get(paymentMethodRejectedType.toLowerCase());
  }
}
