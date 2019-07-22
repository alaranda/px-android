package com.mercadolibre.utils;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ErrorConstantsTest {

    @Test
    public void getGeneralErrorByLanguage_es_generalErrorEs()  {
        final String mesasge = ErrorsConstants.getGeneralErrorByLanguage("es");
        assertThat(mesasge, is(ErrorsConstants.GENERAL_ERROR_ES));
    }

    @Test
    public void getGeneralErrorByLanguage_pt_generalErrorPt()  {
        final String mesasge = ErrorsConstants.getGeneralErrorByLanguage("pt");
        assertThat(mesasge, is(ErrorsConstants.GENERAL_ERROR_PT));
    }

    @Test
    public void getGeneralErrorByLanguage_en_generalErrorEs()  {
        final String mesasge = ErrorsConstants.getGeneralErrorByLanguage("en");
        assertThat(mesasge, is(ErrorsConstants.GENERAL_ERROR_ES));
    }

    @Test
    public void getGeneralErrorByLanguage_ES_generalErrores()  {
        final String mesasge = ErrorsConstants.getGeneralErrorByLanguage("ES");
        assertThat(mesasge, is(ErrorsConstants.GENERAL_ERROR_ES));
    }
}
