package com.mercadolibre.utils;

import org.junit.Test;

import java.util.ResourceBundle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceBundleTest {

    @Test
    public void getGenericError_es_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_ES);
        assertThat(esArBundle.getString("checkout.initpreference.error.generic"), is("No pudimos procesar tu pago, discúlpanos."));
    }

    @Test
    public void getGenericError_pt_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_PT);
        assertThat(esArBundle.getString("checkout.initpreference.error.generic"), is("Desculpe, não foi possível processar o seu pagamento."));
    }

    @Test
    public void getGenericError_en_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_EN);
        assertThat(esArBundle.getString("checkout.initpreference.error.generic"), is("No pudimos procesar tu pago, discúlpanos."));
    }
}
