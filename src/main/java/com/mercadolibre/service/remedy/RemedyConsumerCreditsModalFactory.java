package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.Constants.ACTION_CHANGE_PM;
import static com.mercadolibre.constants.Constants.ACTION_PAY;
import static com.mercadolibre.constants.Constants.BUTTON_LOUD;
import static com.mercadolibre.constants.Constants.BUTTON_QUIET;
import static com.mercadolibre.constants.Constants.CONSUMER_CREDITS_MODAL_TEXT_COLOR;
import static com.mercadolibre.constants.Constants.WEIGHT_BOLD;
import static com.mercadolibre.utils.Translations.REMEDY_MODAL_CONSUMER_CREDITS_DESCRIPTION;
import static com.mercadolibre.utils.Translations.REMEDY_MODAL_CONSUMER_CREDITS_MAIN_BUTTON_LABEL;
import static com.mercadolibre.utils.Translations.REMEDY_MODAL_CONSUMER_CREDITS_SECONDARY_BUTTON_LABEL;
import static com.mercadolibre.utils.Translations.REMEDY_MODAL_CONSUMER_CREDITS_TITLE;

import com.mercadolibre.dto.modal.ModalAction;
import com.mercadolibre.px.dto.lib.button.Button;
import com.mercadolibre.px.dto.lib.text.Text;
import com.mercadolibre.utils.Translations;
import java.util.Locale;

public enum RemedyConsumerCreditsModalFactory {
  INSTANCE;

  public ModalAction build(final Locale locale) {
    return ModalAction.builder()
        .title(buildTitle(locale))
        .description(buildDescription(locale))
        .mainButton(buildMainButton(locale))
        .secondaryButton(buildSecondaryButton(locale))
        .build();
  }

  private Text buildTitle(final Locale locale) {
    return new Text(
        getTranslation(locale, REMEDY_MODAL_CONSUMER_CREDITS_TITLE),
        null,
        CONSUMER_CREDITS_MODAL_TEXT_COLOR,
        WEIGHT_BOLD);
  }

  private Text buildDescription(final Locale locale) {
    return new Text(
        getTranslation(locale, REMEDY_MODAL_CONSUMER_CREDITS_DESCRIPTION),
        null,
        CONSUMER_CREDITS_MODAL_TEXT_COLOR,
        WEIGHT_BOLD);
  }

  private Button buildMainButton(final Locale locale) {
    return Button.builder()
        .label(getTranslation(locale, REMEDY_MODAL_CONSUMER_CREDITS_MAIN_BUTTON_LABEL))
        .action(ACTION_PAY)
        .type(BUTTON_LOUD)
        .build();
  }

  private Button buildSecondaryButton(final Locale locale) {
    return Button.builder()
        .label(getTranslation(locale, REMEDY_MODAL_CONSUMER_CREDITS_SECONDARY_BUTTON_LABEL))
        .action(ACTION_CHANGE_PM)
        .type(BUTTON_QUIET)
        .build();
  }

  private String getTranslation(final Locale locale, final String key) {
    return Translations.INSTANCE.getTranslationByLocale(locale, key);
  }
}
