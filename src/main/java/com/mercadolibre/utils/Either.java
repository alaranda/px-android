package com.mercadolibre.utils;

public final class Either<A, B> {

    private final A value;
    private final B alternative;

    private Either(final A value, final B alternative) {
        this.value = value;
        this.alternative = alternative;
    }

    /**
     * Instantiates an Either object with the primary value, leaving the alternative in null
     *
     * @param value the primary value
     * @param <A> the primary value type
     * @param <B> the alternative value type
     * @return the Either instance with the given primary value
     */
    public static <A, B> Either<A, B> value(final A value) {
        if (value == null) {
            throw new IllegalArgumentException("The value cannot be null.");
        }
        return new Either<>(value, null);
    }

    /**
     * Instantiates an Either object with the right value, leaving the left in null
     *
     * @param alternative the alternative value
     * @param <A> the primary value type
     * @param <B> the alternative value type
     * @return the Either instance with the given alternative value
     */
    public static <A, B> Either<A, B> alternative(final B alternative) {
        if (alternative == null) {
            throw new IllegalArgumentException("The alternative value cannot be null.");
        }
        return new Either<>(null, alternative);
    }

    /**
     * value getter
     *
     * @return the value
     */
    public A getValue() {
        if (!isValuePresent()) {
            throw new IllegalStateException("The value is not present.");
        }
        return value;
    }

    /**
     * alternative value getter
     *
     * @return the alternative value
     */
    public B getAlternative() {
        if (isValuePresent()) {
            throw new IllegalStateException("The alternative value is not present.");
        }
        return alternative;
    }

    /**
     * Checks if the value is present
     *
     * @return true if the value is present, false if this is an alternative
     */
    public boolean isValuePresent() {
        return value != null;
    }
}