package com.mercadolibre.dto.preference;

import java.util.List;
import java.util.OptionalInt;

public class PreferencePaymentMethod {

    private List<PaymentMethod> excludedPaymentMethods;
    private List<PaymentType> excludedPaymentTypes;
    private String defaultPaymentMethodId;
    private OptionalInt installments = OptionalInt.empty();
    private OptionalInt defaultInstallments = OptionalInt.empty();

    PreferencePaymentMethod() {
    }

    public OptionalInt getDefaultInstallments() {
        return defaultInstallments;
    }

    public OptionalInt getInstallments() {
        return installments;
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public List<PaymentMethod> getExcludedPaymentMethods() {
        return excludedPaymentMethods;
    }

    public List<PaymentType> getExcludedPaymentTypes() {
        return excludedPaymentTypes;
    }

    public static final class PaymentMethod {
        private String id;

        public String getId() {
            return id;
        }
    }

    public static final class PaymentType {
        private String id;

        public String getId() {
            return id;
        }
    }
}
