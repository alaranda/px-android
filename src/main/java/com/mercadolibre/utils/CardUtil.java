package com.mercadolibre.utils;

import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.px.dto.lib.card.Cardholder;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.user.Identification;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardUtil {

  private static final List<String> CARD_PAYMENT_TYPE_IDS =
      Arrays.asList("credit_card", "debit_card");
  private static final String[] ALTERNATIVE_IDENTIFICATION_MLA = new String[] {"cuil", "cuit"};
  private static final String PREFIX_ZEROS = "^0+(?!$)";
  private static final int INITIAL_POSITION = 2;
  private static final int LENGTH = 8;

  public static boolean isThirdPartyCard(
      final Identification userIdentification, final Cardholder cardholder) {

    if (isInvalidIdentification(userIdentification)
        || cardholder == null
        || isInvalidIdentification(cardholder.getIdentification())) {
      return false;
    }

    return !castCuilOrCuitToDni(userIdentification)
        .equalsIgnoreCase(castCuilOrCuitToDni(cardholder.getIdentification()));
  }

  public static boolean isCardPaymentFromMLA(final String siteId, final Payment payment) {
    return Site.MLA.getSiteId().equalsIgnoreCase(siteId)
        && payment != null
        && CARD_PAYMENT_TYPE_IDS.contains(payment.getPaymentTypeId())
        && payment.getCard() != null;
  }

  private static String castCuilOrCuitToDni(final Identification identification) {
    if (isAlternativeIdentification(identification)) {
      return StringUtils.mid(identification.getNumber(), INITIAL_POSITION, LENGTH)
          .replaceFirst(PREFIX_ZEROS, "");
    }
    return identification.getNumber();
  }

  private static boolean isAlternativeIdentification(final Identification identification) {
    return Arrays.stream(ALTERNATIVE_IDENTIFICATION_MLA)
        .anyMatch(identification.getType()::equalsIgnoreCase);
  }

  private static boolean isInvalidIdentification(final Identification identification) {
    return identification == null
        || identification.getType() == null
        || identification.getNumber() == null;
  }
}
