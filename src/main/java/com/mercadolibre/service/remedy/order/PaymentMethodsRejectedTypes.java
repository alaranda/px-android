package com.mercadolibre.service.remedy.order;

import java.util.HashMap;
import java.util.Map;

public class PaymentMethodsRejectedTypes {

  public static String ACCOUNT_MONEY = "account_money";
  public static String CONSUMER_CREDITS = "digital_currency";
  public static String CREDIT_CARD = "credit_card";
  public static String DEBIT_CARD = "debit_card";

  private static final Map<String, SuggestionCriteriaInterface> mapSuggestionCriteria =
      new HashMap<String, SuggestionCriteriaInterface>() {
        {
          put(ACCOUNT_MONEY, new AccountMoneyRejected());
          put(CONSUMER_CREDITS, new ConsumerCreditsRejected());
          put(DEBIT_CARD, new DebitCardRejected());
          put(CREDIT_CARD, new CreditCardRejected());
        }
      };

  public SuggestionCriteriaInterface getSuggestionOrderCriteria(
      final String paymentMehtodRejectedType) {
    return mapSuggestionCriteria.get(paymentMehtodRejectedType.toLowerCase());
  }
}
