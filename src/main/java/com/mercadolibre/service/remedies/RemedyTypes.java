package com.mercadolibre.service.remedies;


import com.mercadolibre.api.RiskApi;
import com.mercadolibre.dto.remedies.Remedy;
import com.mercadolibre.utils.RemediesTexts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemedyTypes {

    private static final RemediesTexts REMEDIES_TEXTS = new RemediesTexts();
    private static final RiskApi RISK_API = new RiskApi();

    private static final Map<Remedy, List<RemedyInterface>> mapRemediesInterface = new HashMap<Remedy, List<RemedyInterface>>(){
        {
            put(Remedy.CC_REJECTED_HIGH_RISK, Arrays.asList(new RemedyHighRisk(REMEDIES_TEXTS, RISK_API)));

            put(Remedy.REJECTED_HIGH_RISK, Arrays.asList(new RemedyCvv(), new RemedyBadFilledDate(REMEDIES_TEXTS)));

            put(Remedy.CC_REJECTED_BAD_FILLED_DATE, Arrays.asList(new RemedyBadFilledDate(REMEDIES_TEXTS)));

            put(Remedy.CC_REJECTED_BAD_FILLED_SECURITY_CODE, Arrays.asList(new RemedyCvv()));

            put(Remedy.CC_REJECTED_CALL_FOR_AUTHORIZE, Arrays.asList(new RemedyCallForAuthorize(REMEDIES_TEXTS)));

            put(Remedy.CC_REJECTED_INSUFFICIENT_AMOUNT, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_INSUFFICIENT_AMOUNT.getId())));

            put(Remedy.CC_REJECTED_OTHER_REASON, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_OTHER_REASON.getId())));

            put(Remedy.CC_REJECTED_MAX_ATTEMPTS, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_MAX_ATTEMPTS.getId())));

            put(Remedy.CC_REJECTED_BLACKLIST, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_BLACKLIST.getId())));

            put(Remedy.CC_REJECTED_INVALID_INSTALLMENTS, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_INVALID_INSTALLMENTS.getId())));

            put(Remedy.CC_REJECTED_BAD_FILLED_CARD_NUMBER, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_BAD_FILLED_CARD_NUMBER.getId())));

            put(Remedy.CC_REJECTED_BAD_FILLED_OTHER, Arrays.asList(new RemedySuggestionPaymentMethod(REMEDIES_TEXTS, Remedy.CC_REJECTED_BAD_FILLED_OTHER.getId())));

            put(Remedy.WITHOUT_REMEDY, Arrays.asList(new WithoutRemedy(REMEDIES_TEXTS)));
        }
    };

    public List<RemedyInterface> getRemedyByType(final String type) {
        return mapRemediesInterface.get(Remedy.from(type));
    }
}
