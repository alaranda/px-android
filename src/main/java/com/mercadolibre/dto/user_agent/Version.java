package com.mercadolibre.dto.user_agent;


import com.google.common.base.Objects;

import java.util.regex.Pattern;


public final class Version implements Comparable<Version> {

    private static final String INVALID_VERSION_MESSAGE = "Invalid version format.";
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
    private static final int VERSION_LENGTH = 3;
    public static final Version LOYALTY_LINK_INVALID_VERSION_LESS = Version.create("4.26");


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

    /**
     * This class contains the loyalty ignore version
     */
    public static final class CongratsApi {

        public static final Version WITHOUT_LOYALTY_CONGRATS_IOS = Version.create("4.22");

        public static final Version WITHOUT_LOYALTY_CONGRATS_ANDROID = Version.create("4.23.1");
    }



}
