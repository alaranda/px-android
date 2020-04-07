package com.mercadolibre.dto.remedy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemediesResponse {

  private ResponseCallForAuth callForAuth;
  private ResponseCvv cvv;
  private ResponseBadFilledDate badFilledDate;
  private ResponseRemedyDefault withOutRemedy;
  private ResponseHighRisk highRisk;
  private SuggestionPaymentMethodResponse suggestionPaymentMethod;

  public String toLog(final RemediesResponse remediesResponse) {

    final StringBuilder stringBuilder = new StringBuilder();
    if (null != remediesResponse.getCvv()) {
      appendTitleAndMessage(
          stringBuilder,
          remediesResponse.getCvv().getTitle(),
          remediesResponse.getCvv().getMessage());
      stringBuilder.append(
          String.format("field_setting", remediesResponse.getCvv().getFieldSetting().toString()));
    }
    if (null != remediesResponse.getCallForAuth()) {
      appendTitleAndMessage(
          stringBuilder,
          remediesResponse.getCallForAuth().getTitle(),
          remediesResponse.getCallForAuth().getMessage());
    }
    if (null != remediesResponse.getHighRisk()) {
      appendTitleAndMessage(
          stringBuilder,
          remediesResponse.getHighRisk().getTitle(),
          remediesResponse.getHighRisk().getMessage());
      stringBuilder.append(String.format("deeplink", remediesResponse.getHighRisk().getDeepLink()));
    }
    return stringBuilder.toString();
  }

  private void appendTitleAndMessage(
      final StringBuilder stringBuilder, final String title, final String message) {
    stringBuilder.append(String.format("title: %s - ", title));
    stringBuilder.append(String.format("message: %s - ", message));
  }
}
