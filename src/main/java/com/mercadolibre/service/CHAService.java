package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.GETTING_PARAMETERS;

import com.mercadolibre.api.CardHolderAuthenticationAPI;
import com.mercadolibre.dto.cha.CardHolder;
import com.mercadolibre.dto.cha.CardHolderAuthenticationRequest;
import com.mercadolibre.dto.cha.CardHolderData;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.utils.CardHolderAuthenticationUtils;
import java.util.Calendar;
import org.apache.http.HttpStatus;

/** Card Holder Authentication Service */
public class CHAService {

  private final CardHolderAuthenticationAPI cardHolderAuthenticationAPI;

  public CHAService() {
    this.cardHolderAuthenticationAPI = new CardHolderAuthenticationAPI();
  }

  public Object authenticate(
      final Context context,
      final CardHolderAuthenticationRequest request,
      final String callerId,
      final String cardToken)
      throws ApiException {
    try {

      final Long userId = Long.parseLong(callerId);
      final String purchaseDate =
          CardHolderAuthenticationUtils.formatDate(Calendar.getInstance().getTime());

      final String purchaseAmount =
          CardHolderAuthenticationUtils.formatAmount(
              request.getPurchaseAmount(),
              request.getCurrency().getThousandsSeparator(),
              request.getCurrency().getDecimalSeparator());

      final CardHolderData cardHolderData =
          new CardHolderData()
              .setSdkData(
                  request.getSdkAppId(),
                  request.getSdkEncData(),
                  request.getSdkEphemPubKey(),
                  request.getSdkMaxTimeout(),
                  request.getSdkReferenceNumber(),
                  request.getSdkTransId())
              .setCard(request.getCard())
              .setPurchaseDate(purchaseDate)
              .setPurchaseCurrency(request.getCurrency().getId())
              .setPurchaseAmount(purchaseAmount)
              .setPurchaseExponent(request.getCurrency().getDecimalPlaces())
              .setSiteId(request.getSiteId());

      final CardHolder cardHolder = new CardHolder(cardHolderData, userId);

      // TODO: modelado del response
      return cardHolderAuthenticationAPI.authenticateCard(context, cardToken, cardHolder);
    } catch (final Exception e) {
      throw new ApiException(
          e.getMessage(), GETTING_PARAMETERS, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
