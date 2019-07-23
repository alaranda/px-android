package com.mercadolibre.exceptions;

import com.google.common.collect.ImmutableMap;
import com.mercadolibre.dto.ApiError;
import org.apache.http.HttpStatus;

import java.util.Map;

public class ApiException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final String description;
    private final int statusCode;

    /**
     * Creates a new ApiException
     *
     * @param code        The error code to report
     * @param description The description to report
     */
    public ApiException(final String code, final String description) {
        this(code, description, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a new ApiException
     *
     * @param code        The error code to report
     * @param description The description to report
     * @param exception   The init cause
     */
    public ApiException(final String code, final String description, final Exception exception) {
        this(code, description, HttpStatus.SC_INTERNAL_SERVER_ERROR, exception);
    }

    /**
     * Creates a new ApiException
     *
     * @param code        The error code to report
     * @param description The description to report
     * @param statusCode  The HTTP status code to send back to the client
     * @param exception   The init cause
     */
    public ApiException(final String code, final String description, final int statusCode, final Exception exception) {
        this.code = code;
        this.description = description;
        this.statusCode = statusCode;
        this.initCause(exception);
    }

    /**
     * Creates a new ApiException
     *
     * @param code        The error code to report
     * @param description The description to report
     * @param statusCode  The HTTP status code to send back to the client
     */
    public ApiException(final String code, final String description, final int statusCode) {
        this.code = code;
        this.description = description;
        this.statusCode = statusCode;
    }

    public ApiException(final ApiError error) {
        this(error.getError(), error.getMessage(), error.getStatus());
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get error params for New Relic
     *
     * @return the map containing the error code and description
     */
    public Map<String, String> getNewRelicParams() {
        return ImmutableMap.of(
                "error-code", code,
                "error-description", description,
                "status", String.valueOf(statusCode)
        );
    }

    /**
     * Convert exception to ApiError
     *
     * @return ApiError
     */
    public ApiError toApiError() {
        return new ApiError(this.getDescription(), this.getCode(), this.getStatusCode());
    }

    @Override
    public String toString() {
        return "ApiException{"
                + "code='" + code + '\''
                + ", description='" + description + '\''
                + ", statusCode=" + statusCode
                + '}';
    }
}