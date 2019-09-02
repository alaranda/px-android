package com.mercadolibre.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ClientsIdConstants {

    /**
     * Client Id Default que viene en la preferencia cunado no se lo setean.
     */
    public static final Long CLIENT_ID_DEFAULT = 963L;

    public static final Map<String, Long> CLIENTS_ID_MAP() {
        HashMap<String, Long> clients_id_map = new HashMap<String, Long>();
        clients_id_map.put("MLA-PROD", 951593730614252L);
        clients_id_map.put("MLB-PROD", 3469396897046303L);
        clients_id_map.put("MCO-PROD", 4846351028798241L);
        clients_id_map.put("MLM-PROD", 3485627620160199L);
        clients_id_map.put("MLC-PROD", 7316776222058661L);
        clients_id_map.put("MLU-PROD", 7672567901099495L);
        clients_id_map.put("MLV-PROD", 4525909730898960L);
        clients_id_map.put("MPE-PROD", 3430534215120943L);
        clients_id_map.put("MLA-TEST", 7687973613171611L);
        clients_id_map.put("MLB-TEST", 6506927170233259L);
        clients_id_map.put("MCO-TEST", 7569143650399920L);
        clients_id_map.put("MLM-TEST", 6122032597128314L);
        clients_id_map.put("MLC-TEST", 5680749691391805L);
        clients_id_map.put("MLU-TEST", 2997350124650644L);
        clients_id_map.put("MLV-TEST", 6922026411226764L);
        clients_id_map.put("MPE-TEST", 3430534215120943L);
        Map<String, Long> unmodifiableCLientsIdMap = Collections.unmodifiableMap(clients_id_map);
        return unmodifiableCLientsIdMap;
    }
}
