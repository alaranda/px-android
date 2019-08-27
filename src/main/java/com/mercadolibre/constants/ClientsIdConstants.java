package com.mercadolibre.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class ClientsIdConstants {

    /**
     * Client Id Default que viene en la preferencia cunado no se lo setean.
     */
    public static final Long CLIENT_ID_DEFAULT = 963L;

    public static final Map<String, Long> SITES_CLIENTS_ID() {
        return ImmutableMap.of(
                "MLA", 000L,
                "MLB", 000L,
                "MLM", 123L
        );
    }

}
