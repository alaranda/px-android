package com.mercadolibre.service.remedy.order;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import java.util.List;
import java.util.Map;

public interface SuggestionCriteriaInterface {

  public PaymentMethodSelected findBestMedium(
      final RemediesRequest remediesRequest,
      final Map<String, List<AlternativePayerPaymentMethod>> payerPaymentMethodsMa);
}
