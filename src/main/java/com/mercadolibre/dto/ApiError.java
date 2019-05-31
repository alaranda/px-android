package com.mercadolibre.dto;

import com.google.gson.annotations.SerializedName;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpStatus;

import java.util.List;

public final class ApiError {

    public static final ApiError GENERIC = new ApiError("error", "generic", HttpStatus.SC_BAD_REQUEST);

    public static final ApiError EXTERNAL_API = new ApiError(
        "External API Error", "external_api_error", HttpStatus.SC_BAD_GATEWAY);

    private String message;
    private String error;
    private int status;
    @SerializedName("cause")
    private List<Cause> causes;

    /**
     * ApiError constructor
     *
     * @param message the message
     * @param error the error
     * @param status the status
     */
    public ApiError(final String message, final String error, final int status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }

    /* default */ ApiError() {
        // nothing to do
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Cause> getCauses() {
        return causes;
    }

    @Override
    public String toString() {
        return "ApiError{"
            + "message='" + message + '\''
            + ", error='" + error + '\''
            + ", status=" + status
            + ", causes=" + causes
            + '}';
    }

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ApiError apiError = (ApiError) o;
        if (status != apiError.status) {
            return false;
        }
        if (message != null ? !message.equals(apiError.message) : apiError.message != null) {
            return false;
        }
        return error != null ? error.equals(apiError.error) : apiError.error == null;
    }

    @Override
    @SuppressWarnings({ "checkstyle:magicnumber", "PMD.ConfusingTernary" })
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + status;
        return result;
    }

    public static class Cause {

        private int code;
        @SuppressFBWarnings(value = {"UWF_UNWRITTEN_FIELD"}, justification = "Gson writes it")
        private String description;

        /* default */ Cause() {
            // nothing to be done
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "Cause{"
                + "code=" + code
                + ", description='" + description + '\''
                + '}';
        }
    }

}