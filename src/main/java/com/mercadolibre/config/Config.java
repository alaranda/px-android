package com.mercadolibre.config;

import org.apache.commons.configuration.*;

import java.util.function.Function;

public final class Config {

    private static final String SEP = "-";
    private static final Configuration CONFIG;
    private static final String SCOPE;

    static {
        try {
            final CompositeConfiguration configuration = new CompositeConfiguration();
            configuration.addConfiguration(new PropertiesConfiguration("application.properties"));
            CONFIG = configuration;

            final String scope = System.getenv("SCOPE");
            SCOPE = scope == null ? "" : scope;

        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Config() {
        throw new AssertionError("Can't instantiate a Config class");
    }

    private static <T> T getConfig(final String key, final String scope,
            final Function<String, T> f) {
        final String actualKey;
        if (CONFIG.containsKey(key)) {
            actualKey = key;
        } else {
            actualKey = key + SEP + scope;
        }

        return f.apply(actualKey);
    }

    private static <T> T getConfig(final String key, final Function<String, T> f) {
        return getConfig(key, SCOPE, f);
    }


    /**
     * Get a int associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated int.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Integer.
     */
    public static int getInt(final String key) {
        return getConfig(key, CONFIG::getInt);
    }

    /**
     * Get a string associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated string.
     *
     * @throws ConversionException is thrown if the key maps to an object that
     *         is not a String.
     */
    public static String getString(final String key) {
        return getConfig(key, CONFIG::getString);
    }


    /**
     * Get a long associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated long.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Long.
     */
    public static long getLong(final String key) {
        return getConfig(key, CONFIG::getLong);
    }

    public static String getSCOPE() {
        return SCOPE;
    }
}