package com.mercadolibre.utils;

import org.junit.Test;

import java.util.ResourceBundle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceBundleTest {

    @Test
    public void getGenericError_es_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_ES);
        assertThat(esArBundle.getString("checkout.initpreference.error.generic"), is("No pudimos procesar tu pago, disculpanos."));
    }

    @Test
    public void getGenericError_pt_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_PT);
        assertThat(esArBundle.getString("checkout.initpreference.error.generic"), is("Desculpe, não foi possível processar o seu pagamento."));
    }

    @Test
    public void getGenericError_en_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_EN);
        assertThat(esArBundle.getString("checkout.initpreference.error.generic"), is("We're sorry, we could not process your payment."));
    }

    @Test
    public void getInvalidPreferenceError_es_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_ES);
        assertThat(esArBundle.getString("checkout.initpreference.error.invalidpreference"), is("No puedes pagar con este link de pago."));
    }

    @Test
    public void getInvalidPreferenceError_pt_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_PT);
        assertThat(esArBundle.getString("checkout.initpreference.error.invalidpreference"), is("Não é possível pagar com este link de pagamento."));
    }

    @Test
    public void getInvalidPreferenceErrorr_en_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_EN);
        assertThat(esArBundle.getString("checkout.initpreference.error.invalidpreference"), is("We could not process your payment."));
    }

    @Test
    public void getPayerEqualsCollectorError_es_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_ES);
        assertThat(esArBundle.getString("checkout.initpreference.error.payerequalscollector"), is("No puedes pagar con este link, solo puedes usarlo para cobrar."));
    }

    @Test
    public void getPayerEqualsCollectorError_pt_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_PT);
        assertThat(esArBundle.getString("checkout.initpreference.error.payerequalscollector"), is("Não é possível pagar com este link, Você só pode usá-lo para receber pagamentos."));
    }

    @Test
    public void getPayerEqualsCollectorError_en_validTransaltion()  {
        final ResourceBundle esArBundle = ResourceBundle.getBundle("CustomErrors", ContextUtilsTestHelper.LOCALE_EN);
        assertThat(esArBundle.getString("checkout.initpreference.error.payerequalscollector"), is("You cant pay using this link, this is just available to collect payments."));
    }
}
