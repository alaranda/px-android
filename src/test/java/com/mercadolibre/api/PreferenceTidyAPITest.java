package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.API_CALL_PREFERENCE_TIDY_FAILED;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.mercadolibre.dto.preference.PreferenceTidy;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.exception.RestException;
import java.io.IOException;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

public class PreferenceTidyAPITest extends RestClientTestBase {

  private final Context context = Context.builder().requestId(UUID.randomUUID().toString()).build();
  private final PreferenceTidyAPI preferenceTidyAPI = PreferenceTidyAPI.INSTANCE;

  @Test
  public void getPreferenceByKey_ok() throws IOException, ApiException {
    MockPreferenceTidyAPI.getPreferenceByKey(
        "23BYCZ",
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/preferenceTidy/23BYCZ.json")));

    final PreferenceTidy preferenceTidy = preferenceTidyAPI.getPreferenceByKey(context, "23BYCZ");

    assertThat(preferenceTidy, notNullValue());
  }

  @Test
  public void getPreferenceByKey_throwsException() throws IOException, RestException {
    try {
      MockPreferenceTidyAPI.getPreferenceByKeyFail("23BYCZ");

      preferenceTidyAPI.getPreferenceByKey(context, "23BYCZ");
      fail("should fail");
    } catch (ApiException e) {
      assertEquals(e.getDescription(), API_CALL_PREFERENCE_TIDY_FAILED);
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_GATEWAY);
      assertEquals(e.getCode(), EXTERNAL_ERROR);
    }
  }

  @Test
  public void getPreferenceByKey_fail() throws IOException, RestException {
    try {
      MockPreferenceTidyAPI.getPreferenceByKey(
          "23BYCZ",
          HttpStatus.SC_NOT_FOUND,
          IOUtils.toString(
              getClass().getResourceAsStream("/preferenceTidy/preferenceTidyNotFound.json")));

      preferenceTidyAPI.getPreferenceByKey(context, "23BYCZ");
      fail("should fail");
    } catch (ApiException e) {
      assertEquals(e.getStatusCode(), HttpStatus.SC_NOT_FOUND);
      assertEquals(e.getCode(), "not_found");
    }
  }
}
