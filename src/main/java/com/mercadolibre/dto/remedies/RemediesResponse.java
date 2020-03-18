package com.mercadolibre.dto.remedies;

public class RemediesResponse {

    private ResponseCallForAuth callForAuth;
    private ResponseCvv cvv;
    private ResponseBadFilledDate badFilledDate;
    private ResponseRemedyDefault withOutRemedy;
    private ResponseHighRisk highRisk;
    private SuggestionPaymentMethodResponse suggestionPaymentMethod;

    public RemediesResponse(){};

    public ResponseCallForAuth getResponseCallForAuth() {
        return callForAuth;
    }

    public void setResponseCallForAuth(final ResponseCallForAuth responseCallForAuth) {
        this.callForAuth = responseCallForAuth;
    }

    public ResponseCvv getResponseCvv() {
        return cvv;
    }

    public void setResponseCvv(final ResponseCvv responseCvv) {
        this.cvv = responseCvv;
    }

    public ResponseBadFilledDate getResponseBadFilledDate() {
        return badFilledDate;
    }

    public void setResponseBadFilledDate(final ResponseBadFilledDate responseBadFilledDate) {
        this.badFilledDate = responseBadFilledDate;
    }

    public ResponseRemedyDefault getResponseWithOutRemedy() { return withOutRemedy; }

    public void setResponseWithOutRemedy(final ResponseRemedyDefault responseWithOutRemedy) {
        this.withOutRemedy = responseWithOutRemedy;
    }

    public ResponseHighRisk getHighRisk() {
        return highRisk;
    }

    public void setHighRisk(ResponseHighRisk highRisk) {
        this.highRisk = highRisk;
    }

    public SuggestionPaymentMethodResponse getSuggestionPaymentMethod() {
        return suggestionPaymentMethod;
    }

    public void setSuggestionPaymentMethod(SuggestionPaymentMethodResponse suggestionPaymentMethod) {
        this.suggestionPaymentMethod = suggestionPaymentMethod;
    }

    public String toLog(final RemediesResponse remediesResponse) {

        final StringBuilder stringBuilder =  new StringBuilder();
        if (null  != remediesResponse.getResponseCvv()) {
           appendTitleAndMessage(stringBuilder, remediesResponse.getResponseCvv().getTitle(), remediesResponse.getResponseCvv().getMessage());
           stringBuilder.append(String.format("field_setting", remediesResponse.getResponseCvv().getFieldSetting().toString()));
        }
        if (null != remediesResponse.getResponseCallForAuth()){
            appendTitleAndMessage(stringBuilder, remediesResponse.getResponseCallForAuth().getTitle(), remediesResponse.getResponseCallForAuth().getMessage());
        }
        if (null != remediesResponse.getHighRisk()) {
            appendTitleAndMessage(stringBuilder, remediesResponse.getResponseCallForAuth().getTitle(), remediesResponse.getResponseCallForAuth().getMessage());
            stringBuilder.append(String.format("deeplink", remediesResponse.getHighRisk().getDeepLink()));
        }
        return stringBuilder.toString();
    }


    private void appendTitleAndMessage(final StringBuilder stringBuilder, final String title, final String message) {
        stringBuilder.append(String.format("title: %s - ", title));
        stringBuilder.append(String.format("message: %s - ", message));
    }
}
