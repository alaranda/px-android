package com.mercadolibre.constants;

/**
 * Headers constants
 */
public final class HeadersConstants {
    private HeadersConstants() {
        throw new AssertionError();
    }

    /**
     * Cache prevention values for Cache-Control response header according to MDN:
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#Preventing_caching
     */
    public static final String NO_CACHE_PARAMS = "no-cache, no-store, must-revalidate";

    public static final String X_CALLER_SCOPES = "X-Caller-Scopes";

    public static final String X_REQUEST_ID = "X-Request-Id";

    public static final String SESSION_ID = "X-Session-Id";

    // Header with the product id.
    public static final String PRODUCT_ID = "x-product-id";

    // Header to indicate an api that it is a test.
    public static final String TEST_TOKEN = "X-Test-Token";

    public static final String REQUEST_ID = "request_id";

    public static final String API_CONTEXT = "api_context";

    public static final String IDEMPOTENCY = "x-idempotency-key";

    public static final String MELI_SESSION = "x-meli-session-id";

    public static final String TRACKING = "x-tracking-id";

    public static final String LANGUAGE = "Accept-Language";
}
