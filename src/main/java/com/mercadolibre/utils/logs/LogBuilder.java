package com.mercadolibre.utils.logs;


public class LogBuilder {

    public static final String LEVEL_INFO = "INFO";
    public static final String LEVEL_ERROR = "ERROR";
    public static final String LEVEL_WARNING = "WARNING";

    public static final String REQUEST_IN = "request_in";
    public static final String REQUEST_OUT = "request_out";

    public static final String ERROR_SERVICE = "internal_service_error";
    public static final String SERVICE_WARNING = "service_warning";

    private StringBuilder logs = new StringBuilder();

    /**
     * Level y event son requeridos a la hora de loguear
     *
     * @param level Nivel de criticidad del registro
     * @param event Etiqueta principal de referencia del evento logueado
     */
    public LogBuilder(final String level, final String event) {
        logs.append(String.format("[level: %s] ", level));
        logs.append(String.format("[event: %s] ", event));
    }

    public LogBuilder withSource(final String source) {
        logs.append(String.format("[source: %s] ", source));
        return this;
    }

    public LogBuilder withStatus(final int status) {
        logs.append(String.format("[status: %d] ", status));
        logs.append(String.format("[status_pattern: %s] ", getStatusCodePattern(status)));
        return this;
    }

    public LogBuilder withCallerId(final String callerId) {
        logs.append(String.format("[caller_id: %s] ", callerId));
        return this;
    }

    public LogBuilder withClientId(final String clientId) {
        logs.append(String.format("[client_id: %s] ", clientId));
        return this;
    }

    public LogBuilder withException(final String code, final String description) {
        logs.append(String.format("exception: %s %s - ", code, description));
        return this;
    }

    public LogBuilder withException(final Exception e) {
        logs.append(String.format("exception: %s - ", e.getMessage()));
        return this;
    }

    public LogBuilder withResponse(final String response) {
        logs.append(String.format("Response: %s - ", response));
        return this;
    }

    public LogBuilder withParams(final String params) {
        logs.append(String.format("Params: %s - ", params));
        return this;
    }

    public LogBuilder withMethod(final String method) {
        logs.append(String.format("[method: %s] ", method));
        return this;
    }

    public LogBuilder withUrl(final String url) {
        logs.append(String.format("[url: %s] ", url));
        return this;
    }

    public LogBuilder withHeaders(final String headers) {
        logs.append(String.format("Headers: %s - ", headers));
        return this;
    }

    public LogBuilder withPaymentMethodId(final String paymentMethodId) {
        logs.append(String.format("[payment_method_id: %s] ", paymentMethodId));
        return this;
    }

    public LogBuilder withStatusDetail(final String statusDetail) {
        logs.append(String.format("[status_detail: %s] ", statusDetail));
        return this;
    }

    public LogBuilder withMarketplace(final String marketplace) {
        logs.append(String.format("[marketplace: %s] ", marketplace));
        return this;
    }

    public LogBuilder withSite(final String siteId) {
        logs.append(String.format("[site_id: %s] ", siteId));
        return this;
    }

    public LogBuilder withPref(final String prefId) {
        logs.append(String.format("[pref_id: %s] ", prefId));
        return this;
    }

    /**
     * Adds a custom message to the current log
     *
     * @param message A message
     * @return LogBuilder
     */
    public LogBuilder withMessage(final String message) {
        logs.append(String.format("%s ", message));
        return this;
    }

    public LogBuilder withUserAgent(final String userAgent) {
        logs.append(String.format("[user_agent: %s] ", userAgent));
        return this;
    }

    public LogBuilder withSessionId(final String sessionId) {
        logs.append(String.format("session_id: %s ", sessionId));
        return this;
    }

    public String build() {
        return logs.toString();
    }

    static String getStatusCodePattern(int statusCode) {
        String statusFirstNumber = String.valueOf(statusCode).substring(0, 1);
        return statusFirstNumber + "XX";
    }

}