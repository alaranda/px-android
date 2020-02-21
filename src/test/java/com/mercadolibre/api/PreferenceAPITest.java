package com.mercadolibre.api;

import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.Either;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PreferenceAPITest {

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
    public void getPreference_invalidPreferenceId_notFound() throws IOException {
        try {
            MockPreferenceAPI.getById("1", HttpStatus.SC_NOT_FOUND,
                    IOUtils.toString(getClass().getResourceAsStream("/preference/preferenceNotFound.json")));
            service.geAsynctPreference(context, "1");
        } catch (final ApiException e) {
            assertThat(e.getCode(), is("invalid_id"));
            assertThat(e.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
            assertThat(e.getDescription(), is("preference_id not found"));
        }
    }

}
