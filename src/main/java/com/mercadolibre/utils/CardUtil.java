package com.mercadolibre.utils;

import com.mercadolibre.dto.kyc.UserIdentification;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.px.dto.lib.card.Cardholder;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.utils.CardUtils;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Card related utility operations. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CardUtil {

  private static final List<String> CARD_PAYMENT_TYPE_IDS =
      Arrays.asList("credit_card", "debit_card");

  /**
   * Verify if the card's owner is the same user that's adding it by comparing the card's owner
   * identification to the user's main and other identifications. <br>
   * If card's owner identification type and number does NOT match the same type and number in the
   * user identifications, then the card belongs to a third party.
   *
   * <p>Especially when siteId = MLA, if both type and number is not matched anywhere, we also try
   * to match a DNI number with the middle part of a CUIT, a CUIL or a CDI and vice versa.
   *
   * @param cardholder the card's owner to get his identification
   * @param userIdentification the user that's adding the card, to get its identifications
   * @param siteId the site ID of the application
   * @return true if it's a third party card, false otherwise
   */
  public static boolean isThirdPartyCard(
      final Cardholder cardholder,
      final UserIdentification userIdentification,
      final String siteId) {

    if (cardholder == null || userIdentification == null) {
      return true;
    }

    return CardUtils.verifyUserIsCardOwner(
            cardholder.getIdentification(),
            userIdentification.getIdentification(),
            userIdentification.getPersonOtherIdentifications(),
            siteId)
        .isThirdPartyCard();
  }

  /**
   * Verify if the payment is using a debit or credit card from MLA.
   *
   * @param siteId the site ID where the payment is being made
   * @param payment the payment data to get the payment type
   * @return true if it's a credit or debit card payment in MLA, false otherwise
   */
  public static boolean isCardPaymentFromMla(final String siteId, final Payment payment) {
    return Site.MLA.getSiteId().equalsIgnoreCase(siteId)
        && payment != null
        && CARD_PAYMENT_TYPE_IDS.contains(payment.getPaymentTypeId())
        && payment.getCard() != null;
  }
}
