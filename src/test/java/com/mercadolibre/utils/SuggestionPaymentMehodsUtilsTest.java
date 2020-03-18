package com.mercadolibre.utils;

import com.mercadolibre.dto.remedies.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.px.toolkit.utils.FileParserUtils;
import org.junit.Test;

import static com.mercadolibre.utils.SuggestionPaymentMehodsUtils.findPaymentMethodEqualsAmount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SuggestionPaymentMehodsUtilsTest {


    @Test
    public void findPaymentMethodEqualsAmount_alternativesPms_a() {

        final RemediesRequest remediesRequest = FileParserUtils.getObjectResponseFromFile("/remedies/remedy_request_alternative_pm.json", RemediesRequest.class);

        final AlternativePayerPaymentMethod alternativePayerPaymentMethod = findPaymentMethodEqualsAmount(remediesRequest);

        assertThat(alternativePayerPaymentMethod.getInstallments().size(), is(1));
    }
}
