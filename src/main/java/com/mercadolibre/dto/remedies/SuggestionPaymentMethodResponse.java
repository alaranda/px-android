package com.mercadolibre.dto.remedies;

public class SuggestionPaymentMethodResponse {

    private final String title;
    private final String message;
    private final AlternativePayerPaymentMethod alternativePayerPaymentMethod;

    public SuggestionPaymentMethodResponse(final String title, final String message,
                                           final AlternativePayerPaymentMethod alternativePayerPaymentMethod){
        this.title = title;
        this.message = message;
        this.alternativePayerPaymentMethod = alternativePayerPaymentMethod;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public AlternativePayerPaymentMethod getAlternativePayerPaymentMethod() {
        return alternativePayerPaymentMethod;
    }
}
