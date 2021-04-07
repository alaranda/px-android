package com.mercadolibre.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CardHolderAuthenticationUtils {

  private static final String DATE_FORMAT = "yyyyMMddHHmmss";

  public static String formatAmount(
      final String amount, final Character thousandSeparator, final Character decimalSeparator) {

    String auxAmount = amount.replaceAll("\\" + thousandSeparator.toString(), "");

    if (amount.lastIndexOf(decimalSeparator) == -1) {
      auxAmount += ',';
    }

    auxAmount += "00";

    final String[] amountSplit = auxAmount.split("\\" + decimalSeparator.toString(), 2);

    return amountSplit[0] + amountSplit[1].substring(0, 2);
  }

  public static String formatDate(final Date date) {

    return new SimpleDateFormat(DATE_FORMAT).format(date);
  }

  public static void validatePurchaseAmount(
      final Character decimalSeparator,
      final Character groupingSeparator,
      final String purchaseAmount)
      throws ParseException {
    final DecimalFormat df = new DecimalFormat();
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(decimalSeparator);
    symbols.setGroupingSeparator(groupingSeparator);
    df.setDecimalFormatSymbols(symbols);

    df.parse(purchaseAmount);
  }
}
