package com.mercadolibre.validators;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CollectorsValidate {

    private static final List<Long> COLLECTORS_MELI = new ArrayList<Long>(Arrays.asList(99754138L, 73220027L,
            99628543L, 104328393L, 169885973L, 170120870L, 220115205L, 237674564L, 170120736L));

    /**
     * Valida el collector de la Preference con los collectors de pago de factura de meli.
     *
     * @param collectorId id del collector
     */
    public static boolean containCollector(final Long collectorId) {
        return COLLECTORS_MELI.contains(collectorId) ? true : false;
    }

}
