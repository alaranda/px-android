package com.mercadolibre.service;

import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockUserAPI;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.utils.ContextUtilsTestHelper.CONTEXT_ES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class PreferenceServiceTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    private static final String PREF_MELICOLLECTOR = "127330977-0f03b540-a8c2-4879-af10-66f619786c0c";
    private static final Long USER_ID_1 = 243962506L;
    private static final Long USER_ID_2 = 453962577L;

    @Test
    public void getPreference_collectorMeliEmailPayerDistincEmailPref_ValidationException() throws IOException, InterruptedException, ApiException, ExecutionException {

        MockPreferenceAPI.getById(PREF_MELICOLLECTOR, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));
        MockUserAPI.getById(USER_ID_2, 200,  IOUtils.toString(getClass().getResourceAsStream("/user/453962577.json")));

        try {
            final Preference preference = PreferenceService.INSTANCE.getPreference(CONTEXT_ES, PREF_MELICOLLECTOR, USER_ID_2);
            fail("ValidationException pref");
        } catch (ValidationException e) {
            assertThat(e.getMessage(), is("No puedes pagar con este link de pago. "));
        }
    }

    @Test
    public void getPreference_collectorMeliEmailPayerEqualsEmailPref_200() throws InterruptedException, ApiException, ExecutionException, IOException {

        MockPreferenceAPI.getById(PREF_MELICOLLECTOR, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/127330977-0f03b540-a8c2-4879-af10-66f619786c0c.json")));
        MockUserAPI.getById(USER_ID_1, 200,  IOUtils.toString(getClass().getResourceAsStream("/user/243962506.json")));;

        final Preference preference = PreferenceService.INSTANCE.getPreference(CONTEXT_ES, PREF_MELICOLLECTOR, USER_ID_1);

        assertNotNull(preference);
    }
}
