package com.mercadolibre.utils;

import static org.apache.commons.lang3.LocaleUtils.isAvailableLocale;

import com.mercadolibre.framework.i18n.I18nService;
import java.util.Locale;

public enum Translations {
  INSTANCE;

  public static final String DISCOUNTS = "congrats.discounts";
  public static final String DISCOUNTS_LEVEL = "congrats.discount.level";
  public static final String DISCOUNTS_DOWNLOAD_MP = "congrats.download.app.mp";
  public static final String DISCOUNTS_DOWNLOAD_ML = "congrats.download.app.ml";
  public static final String DOWNLOAD = "congrats.download";
  public static final String SEE_ALL = "congrats.see.all.discounts";
  public static final String PAYMENT_NOT_PROCESSED = "checkout.initpreference.error.generic";
  public static final String CANNOT_PAY_WITH_LINK =
      "checkout.initpreference.error.invalidpreference";
  public static final String CANNOT_PAY_JUST_FOR_COLLECT =
      "checkout.initpreference.error.payerequalscollector";
  public static final String REMEDY_CVV_TITLE = "remedy.cvv.title";
  public static final String REMEDY_CVV_MESSAGE = "remedy.cvv.message";
  public static final String REMEDY_CVV_SUGGESTION_PM_MESSAGE = "remedy.cvv_suggestion_pm.message";
  public static final String REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE =
      "remedy.fieldsetting.hintmessage.title";
  public static final String REMEDY_FIELD_SETTING_CVV_TITLE_BACK =
      "remedy.fieldsetting.cvv.title.back";
  public static final String REMEDY_FIELD_SETTING_CVV_TITLE_FRONT =
      "remedy.fieldsetting.cvv.title.front";
  public static final String REMEDY_HIGH_RISK_TITLE = "remedy.highrisk.title";
  public static final String REMEDY_HIGH_RISK_MESSAGE = "remedy.highrisk.message";
  public static final String REMEDY_HIGH_RISK_BUTTON_LOUD = "remedy.highrisk.button.loud";
  public static final String VIEW_RECEIPT = "view.receipt";
  public static final String IFPE_COMPLIANCE_MESSAGE = "ifpe.compliance_message";
  public static final String REMEDY_INSUFFICIENT_AMOUNT_TITLE = "remedy.insufficient_amount.title";
  public static final String REMEDY_INSUFFICIENT_AMOUNT_MESSAGE =
      "remedy.insufficient_amount.message";
  public static final String REMEDY_OTHER_REASON_TITLE = "remedy.other_reason.title";
  public static final String REMEDY_OTHER_REASON_MESSAGE = "remedy.other_reason.message";
  public static final String REMEDY_MAX_ATTEMPTS_TITLE = "remedy.max_attempts.title";
  public static final String REMEDY_MAX_ATTEMPTS_MESSAGE = "remedy.max_attempts.message";
  public static final String REMEDY_BLACKLIST_TITLE = "remedy.blacklist.title";
  public static final String REMEDY_BLACKLIST_MESSAGE = "remedy.blacklist.message";
  public static final String REMEDY_INVALID_INSTALLMENTS_TITLE =
      "remedy.invalid_installments.title";
  public static final String REMEDY_INVALID_INSTALLMENTS_MESSAGE =
      "remedy.invalid_installments.message";
  public static final String REMEDY_BAD_FILLED_CARD_NUMBER_TITLE =
      "remedy.bad_filled_card_number.title";
  public static final String REMEDY_BAD_FILLED_CARD_NUMBER_MESSAGE =
      "remedy.bad_filled_card_number.message";
  public static final String REMEDY_BAD_FILLED_OTHER_TITLE = "remedy.bad_filled_other.title";
  public static final String REMEDY_BAD_FILLED_OTHER_MESSAGE = "remedy.bad_filled_other.message";
  public static final String REMEDY_CALL_FOR_AUTHORIZE_TITLE = "remedy.call_for_authorize.title";
  public static final String REMEDY_CALL_FOR_AUTHORIZE_MESSAGE =
      "remedy.call_for_authorize.message";
  public static final String REMEDY_CALL_FOR_AUTHORIZE_BUTTON_LOUD =
      "remedy.call_for_authorize.button.loud";
  public static final String REMEDY_GENERIC_TITLE = "remedy.generic.title";

  public static final String EXPENSE_SPLIT_TITLE = "expense.split.title";
  public static final String EXPENSE_SPLIT_BUTTON_TITLE = "expense.split.button.title";

  public static final String RETURNING_MERCHANT_SITE = "congrats.return.merchant.time";
  public static final String RETURN_MERCHANT_SITE = "congrats.return.merchant.button";
  public static final String CONGRATS_THIRD_PARTY_CARD_INFO = "congrats.third.party.card.info";

  public static final String WITH_DEBIT_GENERIC_LABEL = "payment_method.with.debit.label";
  public static final String WITH_CREDIT_GENERIC_LABEL = "payment_method.with.credit.label";
  public static final String TOTAL_PAY_GENERIC_LABEL = "payment_method.total_pay.label";
  public static final String WITH_ACCOUNT_MONEY_GENERIC_LABEL =
      "payment_method.with.account_money.label";

  private final I18nService i18nService = new I18nService();

  /**
   * @param locale Locale
   * @param key String
   * @return String
   */
  public String getTranslationByLocale(Locale locale, final String key) {
    if (!isAvailableLocale(locale)) {
      locale = Locale.forLanguageTag("es-AR");
    }
    // i18nService is expecting literal string
    switch (key) {
      case DISCOUNTS:
        return i18nService.tr("Descuentos por tu nivel", locale);
      case DISCOUNTS_LEVEL:
        return i18nService.tr("descuentos por tu nivel", locale);
      case DISCOUNTS_DOWNLOAD_MP:
        return i18nService.tr("Exclusivo con la app de Mercado Pago", locale);
      case DISCOUNTS_DOWNLOAD_ML:
        return i18nService.tr("Exclusivo con la app de Mercado Libre", locale);
      case DOWNLOAD:
        return i18nService.tr("Descargar", locale);
      case SEE_ALL:
        return i18nService.tr("Ver todos los descuentos", locale);
      case PAYMENT_NOT_PROCESSED:
        return i18nService.tr("No pudimos procesar tu pago, discúlpanos.", locale);
      case CANNOT_PAY_WITH_LINK:
        return i18nService.tr("No puedes pagar con este link de pago.", locale);
      case CANNOT_PAY_JUST_FOR_COLLECT:
        return i18nService.tr(
            "No puedes pagar con este link, solo puedes usarlo para cobrar.", locale);
      case REMEDY_CVV_TITLE:
        return i18nService.tr("El código de seguridad es inválido", locale);
      case REMEDY_CVV_MESSAGE:
        return i18nService.tr("Vuelve a ingresarlo para confirmar el pago.", locale);
      case REMEDY_CVV_SUGGESTION_PM_MESSAGE:
        return i18nService.tr(
            "Te sugerimos ingresar el código de seguridad de tu %s **** %s para reintentar con este medio:",
            locale);
      case REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE:
        return i18nService.tr("Código de seguridad", locale);
      case REMEDY_FIELD_SETTING_CVV_TITLE_BACK:
        return i18nService.tr("Los 3 números están al dorso de tu tarjeta", locale);
      case REMEDY_FIELD_SETTING_CVV_TITLE_FRONT:
        return i18nService.tr("Los 4 números están al frente de tu tarjeta", locale);
      case REMEDY_HIGH_RISK_TITLE:
        return i18nService.tr("Valida tu identidad para realizar el pago", locale);
      case REMEDY_HIGH_RISK_MESSAGE:
        return i18nService.tr(
            "Te pediremos algunos datos. Ten a mano tu DNI. Solo te llevará unos minutos.", locale);
      case REMEDY_HIGH_RISK_BUTTON_LOUD:
        return i18nService.tr("Validar identidad", locale);
      case VIEW_RECEIPT:
        return i18nService.tr("Ver comprobante de pago", locale);
      case IFPE_COMPLIANCE_MESSAGE:
        return i18nService.tr(
            "A partir de ahora, tu cuenta estará bajo la modalidad Mercado Libre IFPE. Usaremos el método de seguridad de tu teléfono para ingresar y pagar con la aplicación.",
            locale);
      case REMEDY_INSUFFICIENT_AMOUNT_TITLE:
        return i18nService.tr(
            "Tus fondos son insuficientes o superaste el límite de compra", locale);
      case REMEDY_INSUFFICIENT_AMOUNT_MESSAGE:
        return i18nService.tr("Te sugerimos reintentar con este medio:", locale);
      case REMEDY_OTHER_REASON_TITLE:
        return i18nService.tr("Tu %s *** %s rechazó el pago", locale);
      case REMEDY_OTHER_REASON_MESSAGE:
        return i18nService.tr("Te sugerimos reintentar con este medio:", locale);
      case REMEDY_MAX_ATTEMPTS_TITLE:
        return i18nService.tr("Llegaste al límite de intentos de pago posibles.", locale);
      case REMEDY_MAX_ATTEMPTS_MESSAGE:
        return i18nService.tr("Te sugerimos reintentar con este medio:", locale);
      case REMEDY_BLACKLIST_TITLE:
        return i18nService.tr("Tu tarjeta está bloqueada.", locale);
      case REMEDY_BLACKLIST_MESSAGE:
        return i18nService.tr(
            "Comunicate con tu banco para solucionarlo. Mientras tanto, te sugerimos reintentar con este medio:",
            locale);
      case REMEDY_INVALID_INSTALLMENTS_TITLE:
        return i18nService.tr("Tu tarjeta no acepta esta cantidad de cuotas.", locale);
      case REMEDY_INVALID_INSTALLMENTS_MESSAGE:
        return i18nService.tr("Te sugerimos reintentar con este medio:", locale);
      case REMEDY_BAD_FILLED_CARD_NUMBER_TITLE:
        return i18nService.tr("El número de tu %s es inválido", locale);
      case REMEDY_BAD_FILLED_CARD_NUMBER_MESSAGE:
        return i18nService.tr("Te sugerimos reintentar con este medio:", locale);
      case REMEDY_BAD_FILLED_OTHER_TITLE:
        return i18nService.tr("Algún dato de tu tarjeta es inválido.", locale);
      case REMEDY_BAD_FILLED_OTHER_MESSAGE:
        return i18nService.tr("Te sugerimos reintentar con este medio:", locale);
      case REMEDY_CALL_FOR_AUTHORIZE_TITLE:
        return i18nService.tr("Tu %s *** %s no autorizó el pago.", locale);
      case REMEDY_CALL_FOR_AUTHORIZE_MESSAGE:
        return i18nService.tr(
            "Llama a %s para autorizar %s a Mercado Pago o reintenta con otro medio de pago.",
            locale);
      case REMEDY_CALL_FOR_AUTHORIZE_BUTTON_LOUD:
        return i18nService.tr("Autorizar el pago.", locale);
      case EXPENSE_SPLIT_TITLE:
        return i18nService.tr("Puedes dividir este gasto con tus contactos", locale);
      case EXPENSE_SPLIT_BUTTON_TITLE:
        return i18nService.tr("Dividir gasto", locale);
      case REMEDY_GENERIC_TITLE:
        return i18nService.tr("Hemos rechazado tu pago", locale);
      case RETURN_MERCHANT_SITE:
        return i18nService.tr("Volver ahora", locale);
      case RETURNING_MERCHANT_SITE:
        return i18nService.tr("Te llevaremos de vuelta al sitio en {0}", locale);
      case WITH_CREDIT_GENERIC_LABEL:
        return i18nService.tr("con crédito", locale);
      case WITH_DEBIT_GENERIC_LABEL:
        return i18nService.tr("con débito", locale);
      case TOTAL_PAY_GENERIC_LABEL:
        return i18nService.tr("Total a pagar", locale);
      case WITH_ACCOUNT_MONEY_GENERIC_LABEL:
        return i18nService.tr("con dinero en Mercado Pago", locale);
      case CONGRATS_THIRD_PARTY_CARD_INFO:
        return i18nService.tr(
            "Por normativa del Banco Central solo guardaremos las tarjetas a tu nombre", locale);
      default:
        return "";
    }
  }
}
