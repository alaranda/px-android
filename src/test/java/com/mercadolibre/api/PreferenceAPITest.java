package com.mercadolibre.api;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.mocks.MockPreferenceAPI;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PreferenceAPITest {

    final PreferenceAPI service = PreferenceAPI.INSTANCE;
    private static final String PREFERENCE_ID = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";

    @Test
    public void getPreference_validPreferenceId_isOk() throws ApiException, IOException {
        MockPreferenceAPI.getById(PREFERENCE_ID, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")));
        final Optional<Preference> preferenceOpt = service.getPreference(UUID.randomUUID().toString(), PREFERENCE_ID);

        final Preference preference = preferenceOpt.get();
        assertThat(preference.getTotalAmount(), is(BigDecimal.valueOf(4823)));
        assertThat(preference.getExternalReference(), is(""));
        assertThat(preference.getCollectorId(), is(138275050L));
    }

    @Test
    public void getPreference_invalidPreferenceId_notFound() throws IOException {
        try {
            MockPreferenceAPI.getById("1", HttpStatus.SC_NOT_FOUND,
                    IOUtils.toString(getClass().getResourceAsStream("/preference/1.json")));
            service.getPreference(UUID.randomUUID().toString(), "1");
        } catch (final ApiException e) {
            assertThat(e.getCode(), is("invalid_id"));
            assertThat(e.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
            assertThat(e.getDescription(), is("preference_id not found"));
        }
    }

}
