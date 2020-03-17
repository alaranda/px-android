package com.mercadolibre.api;

import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.RestClientTestBase;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PreferenceAPITest extends RestClientTestBase {

    final PreferenceAPI service = PreferenceAPI.INSTANCE;
    private static final String PREFERENCE_ID = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    private final Context context = Context.builder().requestId(UUID.randomUUID().toString()).build();

    @Test
    public void getPreference_validPreferenceId_isOk() throws ApiException, IOException, ExecutionException, InterruptedException {
        MockPreferenceAPI.getById(PREFERENCE_ID, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")));
        final CompletableFuture<Either<Preference, ApiError>> futurePref = service.geAsynctPreference(context, PREFERENCE_ID);
        final Preference preference = futurePref.get().getValue();

        assertThat(preference.getTotalAmount(), is(BigDecimal.valueOf(4823)));
        assertThat(preference.getExternalReference(), is(""));
        assertThat(preference.getCollectorId(), is(138275050L));
    }

    @Test
    public void getPreference_invalidPreferenceId_notFound() throws ApiException, IOException, ExecutionException, InterruptedException {
        MockPreferenceAPI.getById("1", HttpStatus.SC_NOT_FOUND,
                IOUtils.toString(getClass().getResourceAsStream("/preference/preferenceNotFound.json")));
        final CompletableFuture<Either<Preference, ApiError>> futurePref = service.geAsynctPreference(context, "1");
        ApiError error = futurePref.get().getAlternative();
        assertFalse(futurePref.get().isValuePresent());
        assertNotNull(error);
        assertEquals(error.getStatus(), HttpStatus.SC_NOT_FOUND);
        assertEquals(error.getError(), "invalid_id");
        assertEquals(error.getMessage(), "preference_id not found");
    }

}
