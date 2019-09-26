package com.mercadolibre.dto.user_agent;


import com.google.common.base.Objects;

import java.util.regex.Pattern;


public final class Version implements Comparable<Version> {

    private static final String INVALID_VERSION_MESSAGE = "Invalid version format.";
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
    private static final int VERSION_LENGTH = 3;


    private final String versionName;
    private final int[] versionParts;

    /**
     * Version name in format major.mid.minor
     *
     * @param versionName a string param like '4.3.1'
     * @return a version instance
     */
    public static Version create(final String versionName) {
        return new Version(versionName);
    }


    private Version(final String versionName) {
        this.versionName = versionName;

        final String[] parts = DOT_PATTERN.split(versionName);
        if (parts.length > VERSION_LENGTH) {
            throw new IllegalArgumentException(INVALID_VERSION_MESSAGE);
        }
        versionParts = new int[VERSION_LENGTH];
        for (int i = 0; i < parts.length; i++) {
            versionParts[i] = Integer.parseInt(parts[i]);
        }
    }

    /**
     * compare two versions return -1 if other mayor this; 0 if equals; 1 if other minor this
     *
     * @param other other version
     * @return int
     */
    @Override
    public int compareTo(final Version other) {
        if (other == null) {
            return 1;
        }
        for (int i = 0; i < VERSION_LENGTH; i++) {
            if (versionParts[i] > other.versionParts[i]) {
                return 1;
            } else if (versionParts[i] < other.versionParts[i]) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version) o;
        return Objects.equal(versionName, version.versionName);
    }

    @Override
    public int hashCode() {
        return versionName.hashCode() * 31;
    }

    public String getVersionName() {
        return versionName;
    }

    public static final class OneTapApi {

        public static final Version LAST_API_VERSION = Version.create("1.0");

        private OneTapApi() {
            // Do nothing
        }
    }

    public static final class MobileApi {

        public static final Version NATIVE_ACCOUNT_MONEY_VERSION = Version.create("1.8");
        public static final Version DISCOUNTS_ENABLED_VERSION = Version.create("1.9");
        public static final Version NO_CARDS_VERSION = Version.create("2.0");
        public static final Version LAST_API_VERSION = NO_CARDS_VERSION;
        public static final Version DISABLED_PAYER_COSTS_VERSION = Version.create("1.9");
        public static final Version DEFAULT_VERSION = Version.create("1.9");

        private MobileApi() {
            // Do nothing
        }
    }

    /**
     * This class contains the mobile app version for certain features
     */
    public static final class Frontend {

        /**
         * Default version assigned when no app version is included in the received user agent
         */
        public static final Version NO_VERSION = Version.create("0.0");

        /**
         * Soldout with campaign information iOS version
         */
        public static final Version SOLDOUT_CAMPAIGN_IOS = Version.create("4.8");

        /**
         * Soldout with campaign information Android version
         */
        public static final Version SOLDOUT_CAMPAIGN_ANDROID = Version.create("4.6.2");

        /**
         * iOS valid amount decimals: Until this version we have problems rounding amount´s decimals in IOS
         * non inclusive version.
         */
        public static final Version IOS_VALID_AMOUNT_DECIMALS = new Version("4.16.0");

        private Frontend() {
            // Do nothing
        }
    }
}
