package com.mercadolibre.service.remedy.order;

import static com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod.CREDIT_CARD_ESC;
import static com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod.CREDIT_CARD_WITHOUT_ESC;
import static com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod.DEBIT_CARD_ESC;
import static com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod.DEBIT_CARD_WITHOUT_ESC;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.ACCOUNT_MONEY;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CONSUMER_CREDITS;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreditCardRejected implements SuggestionCriteriaInterface {

  @Override
  public PaymentMethodSelected findBestMedium(
      final RemediesRequest remediesRequest,
      final Map<String, List<AlternativePayerPaymentMethod>> payerPaymentMethodsMap) {

    final List<AlternativePayerPaymentMethod> paymentMethodsOrdered = new ArrayList<>();
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(CREDIT_CARD_ESC));
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(ACCOUNT_MONEY));
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(DEBIT_CARD_ESC));
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(CREDIT_CARD_WITHOUT_ESC));
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(DEBIT_CARD_WITHOUT_ESC));
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(CONSUMER_CREDITS));

    return SuggestionPaymentMehodsUtils.getPaymentMethodSelected(paymentMethodsOrdered);
  }
}
