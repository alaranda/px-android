package com.mercadolibre.service.remedy.order;

import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.ACCOUNT_MONEY;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DebitCardRejected implements SuggestionCriteriaInterface {

  @Override
  public PaymentMethodSelected findBestMedium(
      final RemediesRequest remediesRequest,
      final Map<String, List<AlternativePayerPaymentMethod>> payerPaymentMethodsMap) {

    final List<AlternativePayerPaymentMethod> paymentMethodsOrdered = new ArrayList<>();
    paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(ACCOUNT_MONEY));
    /*paymentMethodsOrdered.addAll(payerPaymentMethodsMap.get(CONSUMER_CREDITS));*/
    paymentMethodsOrdered.addAll(
        payerPaymentMethodsMap.get(RemedySuggestionPaymentMethod.CREDIT_CARD_ESC));
    paymentMethodsOrdered.addAll(
        payerPaymentMethodsMap.get(RemedySuggestionPaymentMethod.DEBIT_CARD_ESC));
    paymentMethodsOrdered.addAll(
        payerPaymentMethodsMap.get(RemedySuggestionPaymentMethod.DEBIT_CARD_WITHOUT_ESC));
    paymentMethodsOrdered.addAll(
        payerPaymentMethodsMap.get(RemedySuggestionPaymentMethod.CREDIT_CARD_WITHOUT_ESC));

    return SuggestionPaymentMehodsUtils.getPaymentMethodSelected(paymentMethodsOrdered);
  }
}
