package com.mercadolibre.dto.user_agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Objects;

import static com.mercadolibre.px.toolkit.utils.logs.LogBuilder.requestInLogBuilder;

/**
 * User Agent DTO
 */
public final class UserAgent {

    public static final String PX_PRODUCT_NAME = "PX";
    private static final Logger logger = LogManager.getLogger();
    private static final String SEPARATOR = "/";

    /**
     * Default version assigned when no app version is included in the received user agent
     */
    public static final Version NO_VERSION = Version.create("0.0");

    /**
     * Gets an user agent object from an user agent string
     *
     * @param headerValue user agent string
     * @return A user agent object. If the received user agent string is wrong it returns an user
     * agent with default values
     */
    public static UserAgent create(final String headerValue) {
        String product = PX_PRODUCT_NAME;
        OperatingSystem operatingSystem = OperatingSystem.NO_OS;
        Version version = NO_VERSION;

        if (headerValue != null) {
            final String[] mobileVersionArray = headerValue.split(SEPARATOR);

            if (mobileVersionArray.length == 3) {
                try {
                    product = mobileVersionArray[0];
                    operatingSystem = OperatingSystem.valueOf(mobileVersionArray[1].toUpperCase(Locale.ENGLISH).trim());
                    version = Version.create(mobileVersionArray[2]);
                } catch (IllegalArgumentException e) {
                    logger.error(requestInLogBuilder()
                            .withSource(UserAgent.class.getSimpleName())
                            .withMessage("Invalid User Agent, values set to default")
                            .build());
                }
            }
        }

        return new UserAgent(product, operatingSystem, version);
    }


    private final String product;
    private final OperatingSystem operatingSystem;
    private final Version version;

    private UserAgent(final String product, final OperatingSystem operatingSystem, final Version version) {
        this.product = product;
        this.operatingSystem = operatingSystem;
        this.version = version;
    }

    public String getProduct() {
        return product;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return product + "/" + operatingSystem.getName() + "/" + version.getVersionName();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) return false;
        final UserAgent userAgent = UserAgent.class.cast(o);
        return product.equals(userAgent.product) &&
                operatingSystem == userAgent.operatingSystem &&
                version.equals(userAgent.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, operatingSystem, version);
    }
}
