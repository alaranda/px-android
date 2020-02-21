package com.mercadolibre.utils;

import com.mercadolibre.px.dto.lib.context.Context;

import java.util.Locale;
import java.util.UUID;

public class ContextUtilsTestHelper {

    public static final String REQUEST_ID = UUID.randomUUID().toString();
    public static final java.util.Locale LOCALE_ES = new java.util.Locale("es", "AR");
    public static final java.util.Locale LOCALE_PT = new java.util.Locale("pt", "BR");
    public static final java.util.Locale LOCALE_EN = new Locale("en", "US");
    public static final Context CONTEXT_ES = Context.builder().requestId(REQUEST_ID).locale(LOCALE_ES).build();
    public static final Context CONTEXT_PT = Context.builder().requestId(REQUEST_ID).locale(LOCALE_PT).build();
    public static final Context CONTEXT_EN = Context.builder().requestId(REQUEST_ID).locale(LOCALE_EN).build();

}
