package com.mercadolibre.service.remedy;

import static com.mercadolibre.utils.Translations.*;

import com.mercadolibre.api.RiskApi;
import com.mercadolibre.dto.remedy.Remedy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemedyTypes {

  private static final RiskApi RISK_API = new RiskApi();

  private static final Map<Remedy, List<RemedyInterface>> mapRemediesInterface =
      new HashMap<Remedy, List<RemedyInterface>>() {
        {
          put(Remedy.CC_REJECTED_HIGH_RISK, Arrays.asList(new RemedyHighRisk(RISK_API)));

          put(Remedy.REJECTED_HIGH_RISK, Arrays.asList(new RemedyHighRisk(RISK_API)));

          put(Remedy.CC_REJECTED_BAD_FILLED_SECURITY_CODE, Arrays.asList(new RemedyCvv()));

          put(
              Remedy.CC_REJECTED_INSUFFICIENT_AMOUNT,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(),
                      REMEDY_INSUFFICIENT_AMOUNT_TITLE,
                      REMEDY_INSUFFICIENT_AMOUNT_MESSAGE)));

          put(
              Remedy.CC_REJECTED_OTHER_REASON,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(), REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE)));

          put(
              Remedy.CC_REJECTED_MAX_ATTEMPTS,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(), REMEDY_MAX_ATTEMPTS_TITLE, REMEDY_MAX_ATTEMPTS_MESSAGE)));

          put(
              Remedy.CC_REJECTED_BLACKLIST,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(), REMEDY_BLACKLIST_TITLE, REMEDY_BLACKLIST_MESSAGE)));

          put(
              Remedy.CC_REJECTED_INVALID_INSTALLMENTS,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(),
                      REMEDY_INVALID_INSTALLMENTS_TITLE,
                      REMEDY_INVALID_INSTALLMENTS_MESSAGE)));

          put(
              Remedy.CC_REJECTED_BAD_FILLED_CARD_NUMBER,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(),
                      REMEDY_BAD_FILLED_CARD_NUMBER_TITLE,
                      REMEDY_BAD_FILLED_CARD_NUMBER_MESSAGE)));

          put(
              Remedy.CC_REJECTED_BAD_FILLED_OTHER,
              Arrays.asList(
                  new RemedySuggestionPaymentMethod(
                      new RemedyCvv(),
                      REMEDY_BAD_FILLED_OTHER_TITLE,
                      REMEDY_BAD_FILLED_OTHER_MESSAGE)));

          put(Remedy.CC_REJECTED_CALL_FOR_AUTHORIZE, Arrays.asList(new RemedyCallForAuthorize()));

          put(Remedy.CC_REJECTED_BAD_FILLED_DATE, Arrays.asList(new WithoutRemedy()));

          put(Remedy.WITHOUT_REMEDY, Arrays.asList(new WithoutRemedy()));
        }
      };

  public List<RemedyInterface> getRemedyByType(final String type) {
    return mapRemediesInterface.get(Remedy.from(type));
  }
}
