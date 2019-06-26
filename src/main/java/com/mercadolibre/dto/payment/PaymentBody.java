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
    }

    public static Builder builder(final PaymentRequestBody paymentRequestBody, final Preference preference,
                                  final boolean isBLacklabel) {
        return new Builder(paymentRequestBody, preference, isBLacklabel);
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

        Builder(final PaymentRequestBody paymentRequestBody, final Preference preference, final boolean isBLackLabel) {
            this.token = paymentRequestBody.getToken();
            this.issuerId = paymentRequestBody.getIssuerId();
            if(paymentRequestBody.getInstallments() != null) {
                this.installments = paymentRequestBody.getInstallments();
            }
            this.paymentMethodId = paymentRequestBody.getPaymentMethodId();
            this.binaryMode = paymentRequestBody.isBinaryMode();
            this.couponAmount = paymentRequestBody.getCouponAmount();
            this.campaignId = paymentRequestBody.getCampaignId();
            this.couponCode = paymentRequestBody.getCouponCode();

            this.transactionAmount = preference.getTotalAmount();
            this.externalReference = preference.getExternalReference();
            if (preference.getDifferentialPricing() != null) {
                this.differentialPricingId = preference.getDifferentialPricing().getId();
            }

            if (!isBLackLabel) {
                buildPayerWhitelabel(paymentRequestBody, preference);
            }
        }

        private void buildPayerWhitelabel(final PaymentRequestBody paymentRequestBody, final Preference preference) {
            Identification identification = null;
            if (paymentRequestBody.getPayer() != null) {
                final Payer requestPayer = paymentRequestBody.getPayer();
                final String name = (requestPayer.getFirstName() == null) ? requestPayer.getName() : requestPayer.getFirstName();
                final String lastName = (requestPayer.getLastName() == null) ? requestPayer.getSurname() : requestPayer.getLastName();
                this.payer = new PayerBody(name, lastName, paymentRequestBody.getPayer().getEmail(), paymentRequestBody.getPayer().getIdentification());
                identification = paymentRequestBody.getPayer().getIdentification();

            }
            if (identification != null && !identification.getType().equals("CNPJ")){
                if (StringUtils.isBlank(this.payer.firstName) || StringUtils.isBlank(this.payer.lastName)) {
                    if (preference.getPayer() != null) {
                        this.payer = new PayerBody(preference.getPayer().getName(), preference.getPayer().getSurname(),
                                preference.getPayer().getEmail(), preference.getPayer().getIdentification());
                    }
                }
            }
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
