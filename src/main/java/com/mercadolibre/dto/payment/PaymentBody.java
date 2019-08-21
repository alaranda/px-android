package com.mercadolibre.dto.payment;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.Identification;
import com.mercadolibre.dto.Order;
import com.mercadolibre.dto.Payer;
import com.mercadolibre.dto.preference.Preference;
import spark.utils.StringUtils;

import java.math.BigDecimal;

/**
 * Objeto con los parametros que posteamos en el body de payments
 */
public class PaymentBody {

    private String token;

    private String issuerId;
    private Integer installments;

    private String paymentMethodId;

    private BigDecimal transactionAmount;
    private String description;
    private PayerBody payer;
    private Long differentialPricingId;
    private BigDecimal couponAmount;
    private Long campaignId;
    private String couponCode;
    private boolean binaryMode;
    private String externalReference;
    private BasicUser collector;
    private Order order;
    private String marketplace;
    private String operationType;

    public String getToken() {
        return token;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public String getDescription() {
        return description;
    }

    public PayerBody getPayer() {
        return payer;
    }

    public Long getDifferentialPricingId() {
        return differentialPricingId;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public boolean isBinaryMode() {
        return binaryMode;
    }

    public String getExternalReference() {
        return externalReference;
    }

    PaymentBody(final Builder builder) {
        this.externalReference = builder.externalReference;
        this.transactionAmount = builder.transactionAmount;
        this.payer = builder.payer;
        this.installments = builder.installments;
        this.paymentMethodId = builder.paymentMethodId;
        this.issuerId = builder.issuerId;
        this.token = builder.token;
        this.binaryMode = builder.binaryMode;
        this.couponAmount = builder.couponAmount;
        this.campaignId = builder.campaignId;
        this.couponCode = builder.couponCode;
        this.differentialPricingId = builder.differentialPricingId;
        this.collector = builder.collector;
        this.order = builder.order;
        this.marketplace = builder.marketplace;
        this.operationType = builder.operationType;
    }

    public static final class Builder {
        private String token;
        private String issuerId;
        private Integer installments;
        private String paymentMethodId;
        private BigDecimal transactionAmount;
        private String description;
        private PayerBody payer;
        private Long differentialPricingId;
        private BigDecimal couponAmount;
        private Long campaignId;
        private String couponCode;
        private boolean binaryMode;
        private String externalReference;
        private BasicUser collector;
        private Order order;
        private String marketplace;
        private String operationType;

        public static Builder createBlackLabelBuilder(final PaymentData paymentData, final Preference preference){
            final Builder builder = new Builder(preference);
            builder.token = paymentData.hasToken() ? paymentData.getToken().getId() : null;
            builder.issuerId = paymentData.hasIssuer() ? paymentData.getIssuer().getId().toString() : null;
            builder.installments = paymentData.hasPayerCost() ? paymentData.getPayerCost().getInstallments() : null;
            builder.paymentMethodId = paymentData.getPaymentMethod().getId();
            builder.couponAmount = paymentData.hasDiscount() ? paymentData.getDiscount().getCouponAmount() : null;
            builder.campaignId = paymentData.hasCampaignId() ? Long.valueOf(paymentData.getCampaign().getId()) : null;
            builder.payer = new PayerBody(paymentData.getPayer().getName(), paymentData.getPayer().getSurname(), null,  paymentData.getPayer().getIdentification());

            return builder;
        }

        public static Builder createWhiteLabelBuilder(final PaymentData paymentData, final Preference preference) {
            final Builder builder = new Builder(preference);
            builder.token = paymentData.hasToken() ? paymentData.getToken().getId() : null;
            builder.issuerId = paymentData.hasIssuer() ? paymentData.getIssuer().getId().toString() : null;
            builder.installments = paymentData.hasPayerCost() ? paymentData.getPayerCost().getInstallments() : null;
            builder.paymentMethodId = paymentData.getPaymentMethod().getId();
            builder.couponAmount = paymentData. hasDiscount() ? paymentData.getDiscount().getCouponAmount() : null;
            builder.campaignId = paymentData.hasCampaign() ? Long.valueOf(paymentData.getCampaign().getId()) : null;

            return buildPayerWhitelabel(paymentData.getPayer(), preference, builder);
        }

        public static Builder createWhiteLabelLegacyBuilder(final PaymentRequestBody paymentRequestBody, final Preference preference) {
            final Builder builder = new Builder(preference);
            builder.token = paymentRequestBody.getToken();
            builder.issuerId = paymentRequestBody.getIssuerId();
            if(paymentRequestBody.getInstallments() != null) {
                builder.installments = paymentRequestBody.getInstallments();
            }
            builder.paymentMethodId = paymentRequestBody.getPaymentMethodId();
            builder.binaryMode = paymentRequestBody.isBinaryMode();
            builder.couponAmount = paymentRequestBody.getCouponAmount();
            builder.campaignId = paymentRequestBody.getCampaignId();
            builder.couponCode = paymentRequestBody.getCouponCode();

            return buildPayerWhitelabel(paymentRequestBody.getPayer(), preference, builder);
        }

        Builder(final Preference preference) {
            this.transactionAmount = preference.getTotalAmount();
            this.externalReference = preference.getExternalReference();
            this.binaryMode = preference.isBinaryMode();
            this.marketplace = preference.getMarketplace();
            if (preference.getDifferentialPricing() != null) {
                this.differentialPricingId = preference.getDifferentialPricing().getId();
            }
            this.operationType = preference.getOperationType();
        }

        //Validacion para soportar las distintas firmas del front.
        private static Builder buildPayerWhitelabel(final Payer requestPayer, final Preference preference, final Builder builder) {
            Identification identification = null;
            if (requestPayer != null) {
                final String name = (requestPayer.getFirstName() == null) ? requestPayer.getName() : requestPayer.getFirstName();
                final String lastName = (requestPayer.getLastName() == null) ? requestPayer.getSurname() : requestPayer.getLastName();
                builder.payer = new PayerBody(name, lastName, requestPayer.getEmail(),requestPayer.getIdentification());
                identification = requestPayer.getIdentification();

            }
            if (identification != null && !identification.getType().equals("CNPJ")){
                if (StringUtils.isBlank(builder.payer.firstName) || StringUtils.isBlank(builder.payer.lastName)) {
                    if (preference.getPayer() != null) {
                        builder.payer = new PayerBody(preference.getPayer().getName(), preference.getPayer().getSurname(),
                                preference.getPayer().getEmail(), preference.getPayer().getIdentification());
                    }
                }
            }

            return builder;
        }

        public Builder withCollector(long collector) {
            this.collector = new BasicUser(collector);
            return this;
        }

        public Builder withOrder(final long merchantOrderId){
            this.order = new Order(merchantOrderId, Constants.MERCHANT_ORDER_TYPE);
            return this;
        }

        public PaymentBody build() {
            return new PaymentBody(this);
        }
    }

    public static final class PayerBody {
        private String email;
        private String firstName;
        private String lastName;
        private Identification identification;

        public PayerBody() {

        }

        PayerBody(final String firstName, final String lastName, final String email, final Identification identification) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.identification = identification;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }


}
