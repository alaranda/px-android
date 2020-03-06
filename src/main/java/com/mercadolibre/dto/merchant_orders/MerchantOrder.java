package com.mercadolibre.dto.merchant_orders;

import com.mercadolibre.px.dto.lib.item.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

public class MerchantOrder {

    private Long id;
    private String preferenceId;
    private Long collectorId;
    private Long payerId;
    private List<Item> items;
    private String marketplace;
    private String externalReference;
    private BigDecimal shippingCost;
    private Long sponsorId;
    private String notificationUrl;
    private String siteId;
    private BigDecimal totalAmount;
    private String additionalInfo;
    private String orderType;

    private MerchantOrder(final Builder builder) {
        this.id = builder.id;
        this.preferenceId = builder.preferenceId;
        this.collectorId = builder.collectorId;
        this.payerId = builder.payerId;
        this.items = builder.items;
        this.marketplace = builder.marketplace;
        this.externalReference = builder.externalReference;
        this.notificationUrl = builder.notificationUrl;
        this.shippingCost = builder.shippingCost;
        this.siteId = builder.siteId;
        this.totalAmount = builder.totalAmount;
        this.sponsorId = builder.sponsorId;
        this.additionalInfo = builder.additionalInfo;
        this.orderType = builder.orderType;
    }

    /* default */ MerchantOrder() {
        // nothing to be done
    }

    public Long getId() {
        return id;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public String getMarketplace() {
        return marketplace;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public Long getPayerId() {
        return payerId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getSiteId() {
        return siteId;
    }

    public Optional<String> getNotificationUrl() {
        return Optional.ofNullable(notificationUrl);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OptionalLong getSponsorId() {
        return sponsorId == null ? OptionalLong.empty() : OptionalLong.of(sponsorId);
    }

    public Optional<String> getAdditionalInfo() {
        return Optional.ofNullable(additionalInfo);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setCollectorId(final Long collectorId) {
        this.collectorId = collectorId;
    }

    public String getOrderType() {
        return orderType;
    }

    @Override
    @SuppressWarnings("checkstyle:multiplestringliterals")
    public String toString() {
        return "MerchantOrder{"
                + "id=" + id
                + ", preferenceId='" + preferenceId + '\''
                + ", collector=" + collectorId
                + ", payer=" + payerId
                + ", items=" + items
                + ", marketplace='" + marketplace + '\''
                + ", externalReference='" + externalReference
                + ", shippingCost=" + shippingCost
                + ", sponsorId=" + sponsorId
                + ", notificationUrl='" + notificationUrl + '\''
                + ", siteId='" + siteId + '\''
                + ", totalAmount=" + totalAmount
                + ", additionalInfo='" + additionalInfo + '\''
                + '}';
    }

    public static class Builder {

        private Long id;
        private String preferenceId;
        private Long collectorId;
        private Long payerId;
        private List<Item> items;
        private String marketplace;
        private String externalReference;
        private String notificationUrl;
        private BigDecimal shippingCost;
        private Long sponsorId;
        private String siteId;
        private BigDecimal totalAmount;
        private String additionalInfo;
        private Long orderId;
        private String orderType;

        /**
         * Sets the sponsor id
         *
         * @param sponsorId the sponsor id
         * @return this
         */
        public Builder withSponsorId(final Long sponsorId) {
            this.sponsorId = sponsorId;
            return this;
        }

        /**
         * Sets the shipping cost
         *
         * @param shippingCost the shipping cost
         * @return this
         */
        public Builder withShippingCost(final BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        /**
         * Sets the site id
         *
         * @param siteId the site id
         * @return this
         */
        public Builder withSiteId(final String siteId) {
            this.siteId = siteId;
            return this;
        }

        /**
         * Sets the total amount
         *
         * @param totalAmount the total amount
         * @return this
         */
        public Builder withTotalAmount(final BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        /**
         * Sets the preference id
         *
         * @param preferenceId the preference id
         * @return this
         */
        public Builder withPreferenceId(final String preferenceId) {
            this.preferenceId = preferenceId;
            return this;
        }

        /**
         * Sets the collector
         *
         * @param collector the collector
         * @return this
         */
        public Builder withCollector(final Long collector) {
            this.collectorId = collector;
            return this;
        }

        /**
         * Sets the payer
         *
         * @param payer the payer
         * @return this
         */
        public Builder withPayer(final Long payer) {
            this.payerId = payer;
            return this;
        }

        /**
         * Sets the items
         *
         * @param items the items
         * @return this
         */
        public Builder withItems(final List<Item> items) {
            this.items = items;
            return this;
        }

        /**
         * Sets the marketplace
         *
         * @param marketplace the marketplace
         * @return this
         */
        public Builder withMarketplace(final String marketplace) {
            this.marketplace = marketplace;
            return this;
        }

        /**
         * Sets the notification url
         *
         * @param notificationUrl the marketplace
         * @return this
         */
        public Builder withNotificationUrl(final String notificationUrl) {
            this.notificationUrl = notificationUrl;
            return this;
        }

        /**
         * Sets the external reference
         *
         * @param externalReference the external reference
         * @return this
         */
        public Builder withExternalReference(final String externalReference) {
            this.externalReference = externalReference;
            return this;
        }

        /**
         * Sets the additional info
         *
         * @param additionalInfo the additional info
         * @return this
         */
        public Builder withAdditionalInfo(final String additionalInfo) {
            this.additionalInfo = Objects.requireNonNull(additionalInfo);
            return this;
        }

        /**
         * Sets the order id
         *
         * @param orderId the merchant order id
         * @return this
         */
        public Builder withOrderId(final Long orderId) {
            this.id = orderId;
            return this;
        }

        /**
         * Sets the order type
         *
         * @param orderType the merchant order id
         * @return this
         */
        public Builder withOrderType(final String orderType) {
            this.orderType = orderType;
            return this;
        }

        /**
         * Builds the merchant order
         *
         * @return the merchant order
         */
        public MerchantOrder buildMerchantOrder() {
            return new MerchantOrder(this);
        }

        @Override
        public String toString() {
            return "Builder{"
                    + "id=" + id
                    + ", preferenceId='" + preferenceId + '\''
                    + ", collectorId=" + collectorId
                    + ", payerId=" + payerId
                    + ", items=" + items
                    + ", marketplace='" + marketplace + '\''
                    + ", externalReference='" + externalReference + '\''
                    + ", notificationUrl='" + notificationUrl + '\''
                    + ", shippingCost=" + shippingCost
                    + ", sponsorId=" + sponsorId
                    + ", siteId='" + siteId + '\''
                    + ", totalAmount=" + totalAmount
                    + ", additionalInfo='" + additionalInfo + '\''
                    + '}';
        }
    }
}
