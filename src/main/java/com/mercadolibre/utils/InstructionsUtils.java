package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.BOLBRADESCO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.OXXO;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_1_2_DAYS;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_INSTANTLY;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_N_DAYS;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_N_HOURS;

import com.mercadolibre.dto.instructions.types.BolbradescoInstruction;
import com.mercadolibre.dto.instructions.types.OxxoInstruction;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.TransactionDetails;
import com.mercadolibre.px.dto.lib.installments.FinancialInstitution;
import com.mercadolibre.px.dto.lib.installments.PaymentMethod;
import com.mercadolibre.px.dto.lib.preference.PointOfInteraction;
import com.mercadolibre.px.dto.lib.site.CurrencyType;
import com.mercadolibre.px.dto.lib.user.Identification;
import com.mercadolibre.px.dto.lib.user.Payer;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang.math.IntRange;

public final class InstructionsUtils {

  private static final char DOT_SEPARATOR = '.';
  private static final char DASH_SEPARATOR = '-';
  private static final char SLASH_SEPARATOR = '/';

  private static final String CPF = "CPF";
  private static final String CNPJ = "CNPJ";
  private static final String CI = "CI";

  /**
   * Get null-safe amount value.
   *
   * @param transactionDetails TransactionDetails.class
   * @param currencyType CurrencyType.class
   * @return an amount value
   */
  public static String getAmount(
      final TransactionDetails transactionDetails, final CurrencyType currencyType) {
    if (null != transactionDetails && null != transactionDetails.getTotalPaidAmount()) {
      return formatAmount(transactionDetails.getTotalPaidAmount(), currencyType);
    }
    return "";
  }

  /**
   * Convert an amount value to string value with decimal/thousands places and symbol.
   *
   * @param amount BigDecimal.class
   * @param currency CurrencyType.class
   * @return an amount value
   */
  public static String formatAmount(final BigDecimal amount, final CurrencyType currency) {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setDecimalSeparator(currency.getDecimalSeparator());
    dfs.setGroupingSeparator(currency.getThousandsSeparator());

    DecimalFormat df = new DecimalFormat();
    df.setDecimalFormatSymbols(dfs);
    df.setMinimumFractionDigits(currency.getDecimalPlaces());

    return currency.getSymbol() + " " + df.format(amount);
  }

  /**
   * Get null-safe accreditation message translated.
   *
   * @param locale Locale.class
   * @param paymentMethod PaymentMethod.class
   * @return the accreditation message or an empty value
   */
  public static String getAccreditationMessage(
      final Locale locale, final PaymentMethod paymentMethod) {

    if (null != paymentMethod) {
      final Integer accreditationTime = paymentMethod.getAccreditationTime();

      if (null == accreditationTime || accreditationTime < 0) {
        return "";
      }

      if (accreditationTime == 0) {
        return Translations.INSTANCE.getTranslationByLocale(locale, ACCREDITATION_TIME_INSTANTLY);
      }

      if (1440 > accreditationTime) {
        return Translations.INSTANCE.getTranslationByLocale(
            locale,
            new Integer[] {(int) Math.ceil(accreditationTime / 60D)},
            ACCREDITATION_TIME_N_HOURS);
      }

      if (2880 >= accreditationTime) {
        return Translations.INSTANCE.getTranslationByLocale(locale, ACCREDITATION_TIME_1_2_DAYS);
      }

      return Translations.INSTANCE.getTranslationByLocale(
          locale,
          new Integer[] {(int) Math.ceil(accreditationTime / (60 * 24D))},
          ACCREDITATION_TIME_N_DAYS);
    }
    return "";
  }

  /**
   * Get null-safe payment code by payment method.
   *
   * @param payment Payment.class
   * @return payment code or an empty value
   */
  public static String getPaymentCode(final Payment payment) {
    switch (payment.getPaymentMethodId()) {
      case BOLBRADESCO:
        return BolbradescoInstruction.BolbradescoPaymentCode.getBolbradescoCode(
            payment.getBarcode());
      case OXXO:
        return OxxoInstruction.OxxoPaymentCode.getOxxoCode(payment.getTransactionDetails());
    }
    return "";
  }

  /**
   * Format the identification number with the correct separators by identification type.
   *
   * @param identification Identification.class
   * @return identification number formatted
   */
  public static String formatPayerIdentificationNumber(final Identification identification) {
    final String number =
        Optional.ofNullable(identification.getNumber()).orElse("").replaceAll("[^0-9]", "");

    switch (Optional.ofNullable(identification.getType()).orElse("")) {
      case CPF:
        if (number.length() == 11) {
          return new StringBuilder(number)
              .insert(3, DOT_SEPARATOR)
              .insert(7, DOT_SEPARATOR)
              .insert(11, DASH_SEPARATOR)
              .toString();
        }
        break;
      case CNPJ:
        if (number.length() == 14) {
          return new StringBuilder(number)
              .insert(2, DOT_SEPARATOR)
              .insert(6, DOT_SEPARATOR)
              .insert(10, SLASH_SEPARATOR)
              .insert(15, DASH_SEPARATOR)
              .toString();
        }
        break;
      case CI:
        if (new IntRange(7, 8).containsInteger(number.length())) {
          StringBuilder sb =
              new StringBuilder(number)
                  .insert(number.length() - 1, DASH_SEPARATOR)
                  .insert(number.length() - 4, DOT_SEPARATOR);

          if (number.length() == 7) {
            return sb.toString();
          }
          return sb.insert(1, DOT_SEPARATOR).toString();
        }
    }

    return number;
  }

  /**
   * Get null-safe activation uri.
   *
   * @param transactionDetails transactionDetails
   * @return activation uri
   */
  public static String getActivationUri(final TransactionDetails transactionDetails) {
    if (null != transactionDetails) {
      return Objects.toString(transactionDetails.getExternalResourceUrl(), "");
    }
    return "";
  }

  /**
   * Get null-safe transaction id.
   *
   * @param transactionDetails TransactionDetails.class
   * @return transaction id
   */
  public static String getTransactionId(final TransactionDetails transactionDetails) {
    if (null != transactionDetails) {
      return Objects.toString(transactionDetails.getPaymentMethodReferenceId(), "");
    }
    return "";
  }

  /**
   * Get null-safe QR code.
   *
   * @param pointOfInteraction PointOfInteraction.class
   * @return QR code
   */
  public static String getQrCode(final PointOfInteraction pointOfInteraction) {
    if (null != pointOfInteraction && null != pointOfInteraction.getTransactionData()) {
      return Objects.toString(pointOfInteraction.getTransactionData().getQrCode(), "");
    }
    return "";
  }

  /**
   * Get null-safe payer identification number.
   *
   * @param payer Payer.class
   * @return identification number
   */
  public static String getPayerIdentificationNumber(final Payer payer) {
    if (null != payer && null != payer.getIdentification()) {
      return formatPayerIdentificationNumber(payer.getIdentification());
    }
    return "";
  }

  /**
   * Get null-safe payer identification type.
   *
   * @param payer Payer.class
   * @return identification type
   */
  public static String getPayerIdentificationType(final Payer payer) {
    if (null != payer && null != payer.getIdentification()) {
      return Objects.toString(payer.getIdentification().getType(), "");
    }
    return "";
  }

  /**
   * Get null-safe company id. Get company id from PaymentMethod if it has the same financial
   * institution from Payment. Otherwise, get the financial institution from Payment.
   *
   * @param paymentMethod PaymentMethod.class
   * @param payment Payment.class
   * @return company id
   */
  public static String getCompany(final PaymentMethod paymentMethod, final Payment payment) {
    if (null != paymentMethod
        && null != paymentMethod.getFinancialInstitutions()
        && null != payment.getTransactionDetails()) {
      return Objects.toString(
          paymentMethod.getFinancialInstitutions().stream()
              .filter(
                  fi ->
                      Objects.equals(
                          fi.getId(), payment.getTransactionDetails().getFinancialInstitution()))
              .map(FinancialInstitution::getDescription)
              .findFirst()
              .orElse(payment.getTransactionDetails().getFinancialInstitution()),
          "");
    }
    return "";
  }
}
