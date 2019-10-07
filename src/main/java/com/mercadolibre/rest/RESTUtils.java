package com.mercadolibre.rest;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.exceptions.UnsuccessfulResponseException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.restclient.RESTPool;
import com.mercadolibre.restclient.RequestBuilder;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.RestClient;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.newRelic.NewRelicInterceptor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.UnaryOperator;
import java.util.zip.GZIPInputStream;

import static com.mercadolibre.constants.ErrorMessagesConstants.CAN_NOT_INSTANTIATE_UTILITY_CLASS;

@SuppressFBWarnings(value = "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY", justification = "Dependant classes")
public final class RESTUtils {

    private final static Logger logger = LogManager.getLogger();

    private static final ConcurrentMap<String, RestClient> REST_CLIENTS = new ConcurrentHashMap<>();
    private static final String DEFAULT_POOL = "__px_checkout_mobile_payments_default_pool__";

    static {
        registerPool(DEFAULT_POOL, pool ->
            pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong(Constants.SERVICE_SOCKET_TIMEOUT_PROPERTY_KEY))
                .withRetryStrategy(new SimpleRetryStrategy(Config.getInt(Constants.SERVICE_RETRIES_PROPERTY_KEY),
                    Config.getLong(Constants.SERVICE_RETRY_DELAY_PROPERTY_KEY)))
        );
    }

    private RESTUtils() {
        throw new AssertionError(CAN_NOT_INSTANTIATE_UTILITY_CLASS);
    }

    /**
     * Tells if the given response is valid, if it has a successful HTTP status code (200-210)
     *
     * @param response the response to evaluate
     * @return a boolean telling if the response was successful
     */
    public static boolean isResponseSuccessful(final Response response) {
        return response.getStatus() >= HttpStatus.SC_OK && response.getStatus() < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    /**
     * Extracts the object contained into a given response.
     *
     * @param response the given response
     * @param clazz the class of the containing object
     * @param <T> the type of the containing object
     * @return the object contained into the given response
     */
    public static <T> T responseToObject(final Response response, final Class<T> clazz) {
        if (isResponseSuccessful(response)) {
            return GsonWrapper.fromJson(getBody(response), clazz);
        } else {
            throw new UnsuccessfulResponseException(
                "The given response must have a successful HTTP status.");
        }
    }

    /**
     * Builds a customized RESTPool instance and registers it for future use
     *
     * @param poolName the name of the instance
     * @param config configuration function to setup custom pool configuration
     */
    public static void registerPool(final String poolName, final UnaryOperator<RESTPool.Builder> config) {
        REST_CLIENTS.computeIfAbsent(poolName, s -> {
            try {
                final RESTPool pool = config.apply(RESTPool.builder())
                        .addInterceptorLast(new NewRelicInterceptor())
                        .withName(poolName).build();
                return RestClient.builder().withPool(pool).disableDefault().build();
            } catch (final IOException e) {
                logger.info(String.format("[method:registerPool] [exception:%s]", e.getMessage(), e));
                throw new RuntimeException("Failed to create pool / REST client for " + poolName, e);
            }
        });
    }

    /**
     * Builds a customized RequestBuilder instance
     *
     * @param poolName the name of the instance
     * @return the RequestBuilder instance
     * @throws RestException if the creation of the instance failed
     */
    public static RequestBuilder newRestRequestBuilder(final String poolName) throws RestException {
        RestClient client = REST_CLIENTS.get(poolName);
        if (client == null) {
            // Avoid runtime errors, but log a proper error
            logger.error("Attempting to use a non existing REST pool '" + poolName + "'");
            client = REST_CLIENTS.get(DEFAULT_POOL);
        }

        return client.withPool(poolName);
    }

    /**
     * Converts a rest client response into an Either object.
     *
     * @param response the rest client response
     * @param successClass the success class (either's value class)
     * @param <S> the Success class
     * @return the either object
     */
    public static <S> Either<S, ApiError> responseToEither(final Response response,
                                                           final Class<S> successClass) {
        if (isResponseSuccessful(response)) {
            return Either.value(GsonWrapper.fromJson(RESTUtils.getBody(response), successClass));
        }
        return Either.alternative(GsonWrapper.fromJson(RESTUtils.getBody(response), ApiError.class));
    }

    /**
     * Gets the response body as String
     *
     * @param response the response
     * @return the response body
     */
    public static String getBody(final Response response) {
        final Optional<Header> encodingHeader = Optional.ofNullable(response.getHeader(HttpHeaders.CONTENT_ENCODING));
        if (encodingHeader.isPresent() && "gzip".equals(encodingHeader.get().getValue())) {
            try {
                return IOUtils.toString(new GZIPInputStream(new ByteArrayInputStream(
                    response.getBytes())), StandardCharsets.UTF_8);
            } catch (final IOException e) {
                logger.error(String.format("[method:getBody] [exception:%s]", e.getMessage(), e));
            }
        }
        if (response.getHeaders().contains(HttpHeaders.CONTENT_TYPE)) {
            return response.getString();
        }
        final byte[] body = response.getBytes();
        if (body != null) {
            return new String(body, StandardCharsets.UTF_8);
        }
        return StringUtils.EMPTY;
    }
}