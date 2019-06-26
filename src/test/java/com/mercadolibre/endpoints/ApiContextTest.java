package com.mercadolibre.endpoints;

import com.mercadolibre.router.ApiContext;
import org.junit.Test;

import static com.mercadolibre.constants.Constants.*;
import static org.junit.Assert.assertEquals;


public final class ApiContextTest {

    @Test
    public void getApiContextFromScope_alpha_isOk() {
        final String scope = ApiContext.getApiContextFromScope(SCOPE_ALPHA);
        assertEquals(SCOPE_ALPHA, scope);
    }

    @Test
    public void getApiContextFromScope_beta_isOk() {
        final String scope = ApiContext.getApiContextFromScope(SCOPE_BETA);
        assertEquals(SCOPE_BETA, scope);
    }

    @Test
    public void getApiContextFromScope_prod_isOk() {
        final String scope = ApiContext.getApiContextFromScope(SCOPE_PROD);
        assertEquals(API_CONTEXT_V1, scope);
    }

    @Test
    public void getApiContextFromScope_scopeNull_localhost() {
        final String scope = ApiContext.getApiContextFromScope(null);
        assertEquals(API_CONTEXT_LOCALHOST, scope);
    }

    @Test
    public void getApiContextFromScope_scopeEmpty_localhost() {
        final String scope = ApiContext.getApiContextFromScope("");
        assertEquals(API_CONTEXT_LOCALHOST, scope);
    }
}
