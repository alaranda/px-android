package com.mercadolibre.validators;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import java.io.IOException;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class PreferencesValidatorTest {

  private PreferencesValidator validator;

  private static final String CALLER_ID_VALID = "11111";
  private static final String CALLER_ID_INVALID = "138275050";

  public static final String REQUEST_ID = UUID.randomUUID().toString();
  public static final Context CONTEXT_ES = MockTestHelper.mockContextLibDto();

  @Before
  public void setUp() {
    this.validator = new PreferencesValidator();
  }

  @Test
  public void preferenceValidator_notShipments_validationSucceed()
      throws IOException, ValidationException {

    final Preference preference =
        GsonWrapper.fromJson(
            IOUtils.toString(
                getClass()
                    .getResourceAsStream(
                        "/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")),
            Preference.class);
    validator.validate(CONTEXT_ES, preference, CALLER_ID_VALID);
  }

  @Test
  public void preferenceValidator_withShipments_validationFail() throws IOException {
    final Preference preference =
        GsonWrapper.fromJson(
            IOUtils.toString(getClass().getResourceAsStream("/preference/prefWithShipments.json")),
            Preference.class);
    try {
      validator.validate(CONTEXT_ES, preference, CALLER_ID_VALID);
      fail("Expected Validation Exception");
    } catch (ApiException e) {
      assertThat(e.getDescription(), is("No podés pagar con este link de pago."));
    }
  }

  @Test
  public void preferenceValidator_payerEqualsCollector_validationFail() throws IOException {
    final Preference preference =
        GsonWrapper.fromJson(
            IOUtils.toString(
                getClass()
                    .getResourceAsStream(
                        "/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")),
            Preference.class);
    try {
      validator.validate(CONTEXT_ES, preference, CALLER_ID_INVALID);
      fail("Expected Validation Exception");
    } catch (ApiException e) {
      assertThat(
          e.getDescription(), is("No podés pagar con este link, solo podés usarlo para cobrar."));
    }
  }
}
