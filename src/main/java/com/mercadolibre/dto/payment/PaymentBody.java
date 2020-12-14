package com.mercadolibre.dto.payment;

import static com.mercadolibre.constants.Constants.INTERNAL_METADATA_BANK_INFO;
import static com.mercadolibre.constants.Constants.MERCADO_PAGO_PIX_ACCOUNT_ID;
import static com.mercadolibre.constants.Constants.MERCADO_PAGO_PIX_ACCOUNT_NAME;
import static com.mercadolibre.constants.Constants.PIX_PAYMENT_METHOD_ID;
import static com.mercadolibre.constants.Constants.PREFERENCE;

import com.mercadolibre.dto.Order;
import com.mercadolibre.dto.User;
import com.mercadolibre.px.dto.lib.preference.CounterCurrency;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.preference.PurposeDescriptor;
import com.mercadolibre.px.dto.lib.preference.Tax;
import com.mercadolibre.px.dto.lib.user.Identification;
import com.mercadolibre.px.dto.lib.user.Payer;
import com.mercadolibre.utils.PaymentMethodUtils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.utils.StringUtils;

/** Objeto con los parametros que posteamos en el body de payments */
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
  private User collector;
  private Order order;
  private String marketplace;
  private String operationType;
  private Map<String, Object> internalMetadata;
  private String notificationUrl;
  private BigDecimal applicationFee;
  private List<Tax> taxes;
  private String conceptId;
  private BigDecimal conceptAmount;
  private Long sponsorId;
  private String statementDescriptor;
  private Date paymentExpirationDate;
  private String purpose;
  private PurposeDescriptor purposeDescriptor;
  private Map<String, Object> metadata;
  private CounterCurrency counterCurrency;
  private AdditionalInfo additionalInfo;

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

  public Order getOrder() {
    return order;
  }

  public boolean isBinaryMode() {
    return binaryMode;
  }

  public String getExternalReference() {
    return externalReference;
  }

  public Map<String, Object> getInternalMetadata() {
    return internalMetadata;
  }

  public AdditionalInfo getAdditionalInfo() {
    return additionalInfo;
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
    this.internalMetadata = builder.internalMetadata;
    this.notificationUrl = builder.notificationUrl;
    this.applicationFee = builder.applicationFee;
    this.taxes = builder.taxes;
    this.conceptId = builder.conceptId;
    this.conceptAmount = builder.conceptAmount;
    this.sponsorId = builder.sponsorId;
    this.statementDescriptor = builder.statementDescriptor;
    // this.paymentExpirationDate = builder.paymentExpirationDate;
    // this.purpose = builder.purpose;
    // this.purposeDescriptor = builder.purposeDescriptor;
    this.metadata = builder.metadata;
    this.counterCurrency = builder.counterCurrency;
    this.description = builder.description;
    this.additionalInfo = builder.additionalInfo;
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
    private User collector;
    private Order order;
    private String marketplace;
    private String operationType;
    private Map<String, Object> internalMetadata;
    private String notificationUrl;
    private BigDecimal applicationFee;
    private List<Tax> taxes;
    private String conceptId;
    private BigDecimal conceptAmount;
    private Long sponsorId;
    private String statementDescriptor;
    private Date paymentExpirationDate;
    private String purpose;
    private PurposeDescriptor purposeDescriptor;
    private Map<String, Object> metadata;
    private CounterCurrency counterCurrency;
    private AdditionalInfo additionalInfo;

    public static Builder createBlackLabelBuilder(
        final PaymentData paymentData,
        final Preference preference,
        final Boolean isSameBankAccountOwner) {
      final Builder builder = new Builder(preference);
      builder.token = paymentData.hasToken() ? paymentData.getToken().getId() : null;
      builder.issuerId =
          paymentData.hasIssuer() ? paymentData.getIssuer().getId().toString() : null;
      builder.installments =
          paymentData.hasPayerCost() ? paymentData.getPayerCost().getInstallments() : null;
      builder.paymentMethodId = PaymentMethodUtils.getPaymentMethodId(paymentData, preference);
      builder.internalMetadata = buildInternalMetadataMap(preference, builder.paymentMethodId);
      builder.additionalInfo = buildAdditionalInfo(builder.paymentMethodId, isSameBankAccountOwner);
      builder.couponAmount =
          paymentData.hasDiscount() ? paymentData.getDiscount().getCouponAmount() : null;
      builder.campaignId =
          paymentData.hasDiscountToken() ? Long.valueOf(paymentData.getDiscount().getId()) : null;
      builder.payer =
          new PayerBody(
              paymentData.getPayer().getName(),
              paymentData.getPayer().getSurname(),
              null,
              paymentData.getPayer().getIdentification());

      return builder;
    }

    public static Builder createWhiteLabelBuilder(
        final PaymentData paymentData, final Preference preference) {
      final Builder builder = new Builder(preference);
      builder.token = paymentData.hasToken() ? paymentData.getToken().getId() : null;
      builder.issuerId =
          paymentData.hasIssuer() ? paymentData.getIssuer().getId().toString() : null;
      builder.installments =
          paymentData.hasPayerCost() ? paymentData.getPayerCost().getInstallments() : null;
      builder.paymentMethodId = paymentData.getPaymentMethod().getId();
      builder.internalMetadata = buildInternalMetadataMap(preference, builder.paymentMethodId);
      builder.couponAmount =
          paymentData.hasDiscount() ? paymentData.getDiscount().getCouponAmount() : null;
      builder.campaignId =
          paymentData.hasDiscountToken() ? Long.valueOf(paymentData.getDiscount().getId()) : null;

      return buildPayerWhitelabel(paymentData.getPayer(), preference, builder);
    }

    public static Builder createWhiteLabelLegacyBuilder(
        final PaymentRequestBody paymentRequestBody, final Preference preference) {
      final Builder builder = new Builder(preference);
      builder.token = paymentRequestBody.getToken();
      builder.issuerId = paymentRequestBody.getIssuerId();
      if (paymentRequestBody.getInstallments() != null) {
        builder.installments = paymentRequestBody.getInstallments();
      }
      builder.paymentMethodId = paymentRequestBody.getPaymentMethodId();
      builder.internalMetadata = buildInternalMetadataMap(preference, builder.paymentMethodId);
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
      this.notificationUrl = preference.getNotificationUrl();
      this.taxes = preference.getTaxes();
      if (null != preference.getMarketplaceFee()
          && preference.getMarketplaceFee().compareTo(BigDecimal.ZERO) > 0) {
        this.applicationFee = preference.getMarketplaceFee();
      }
      this.conceptId = preference.getConceptId();
      this.conceptAmount = preference.getConceptAmount();
      this.sponsorId = preference.getSponsorId();
      this.statementDescriptor = preference.getStatementDescriptor();
      this.paymentExpirationDate = preference.getDateOfExpiration();
      this.purpose = preference.getPurpose();
      this.purposeDescriptor = preference.getPurposeDescriptor();
      if (preference.getItems() != null && !preference.getItems().isEmpty()) {
        this.description = preference.getItems().get(0).getTitle();
      }
      this.metadata = preference.getMetadata();
      this.counterCurrency = preference.getCounterCurrency();
    }

    // Validacion para soportar las distintas firmas del front.
    private static Builder buildPayerWhitelabel(
        final Payer requestPayer, final Preference preference, final Builder builder) {
      Identification identification = null;
      if (requestPayer != null) {
        final String name =
            (requestPayer.getFirstName() == null)
                ? requestPayer.getName()
                : requestPayer.getFirstName();
        final String lastName =
            (requestPayer.getLastName() == null)
                ? requestPayer.getSurname()
                : requestPayer.getLastName();
        builder.payer =
            new PayerBody(
                name, lastName, requestPayer.getEmail(), requestPayer.getIdentification());
        identification = requestPayer.getIdentification();
      }
      if (identification != null && !identification.getType().equals("CNPJ")) {
        if (StringUtils.isBlank(builder.payer.firstName)
            || StringUtils.isBlank(builder.payer.lastName)) {
          if (preference.getPayer() != null) {
            builder.payer =
                new PayerBody(
                    preference.getPayer().getName(),
                    preference.getPayer().getSurname(),
                    preference.getPayer().getEmail(),
                    preference.getPayer().getIdentification());
          }
        }
      }

      return builder;
    }

    public Builder withCollector(final Long collectorId, final Long operatorIdCollector) {
      this.collector = new User(String.valueOf(collectorId), operatorIdCollector);
      return this;
    }

    public Builder withOrder(final Order order) {
      this.order = order;
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

    public PayerBody() {}

    PayerBody(
        final String firstName,
        final String lastName,
        final String email,
        final Identification identification) {
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

  public static final class AdditionalInfo {

    private BankInfo bankInfo;

    public AdditionalInfo() {}

    public AdditionalInfo(Boolean isSameBankAccountOwner) {
      bankInfo = new BankInfo(isSameBankAccountOwner);
    }

    public BankInfo getBankInfo() {
      return bankInfo;
    }

    public static final class BankInfo {

      private Boolean isSameBankAccountOwner;

      public BankInfo() {}

      public BankInfo(Boolean isSameBankAccountOwner) {
        this.isSameBankAccountOwner = isSameBankAccountOwner;
      }

      public Boolean getIsSameBankAccountOwner() {
        return isSameBankAccountOwner;
      }
    }
  }

  private static Map<String, Object> buildInternalMetadataMap(
      final Preference preference, final String paymentMethodId) {

    Map<String, Object> internalMetadata = preference.getInternalMetadata();
    if (null == internalMetadata) {
      internalMetadata = new HashMap<>();
    }
    internalMetadata.put(PREFERENCE, new PaymentPreference(preference.getId(), null));

    if (PIX_PAYMENT_METHOD_ID.equals(paymentMethodId)) {
      BankInfo bankInfoBody =
          new BankInfo(MERCADO_PAGO_PIX_ACCOUNT_ID, MERCADO_PAGO_PIX_ACCOUNT_NAME);
      internalMetadata.put(INTERNAL_METADATA_BANK_INFO, bankInfoBody);
    }

    return internalMetadata;
  }

  private static AdditionalInfo buildAdditionalInfo(
      final String paymentMethodId, final Boolean isSameBankAccountOwner) {
    if (PIX_PAYMENT_METHOD_ID.equals(paymentMethodId)) {
      return new AdditionalInfo(isSameBankAccountOwner);
    }
    return null;
  }
}
