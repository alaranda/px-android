package com.mercadolibre.dto.payment;

import java.util.List;

public class Issuer {

    private Long id;
    private String name;
    private List<String> labels;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getLabels() {
        return labels;
    }
}
