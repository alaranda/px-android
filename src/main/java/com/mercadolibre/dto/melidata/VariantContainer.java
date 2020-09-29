package com.mercadolibre.dto.melidata;

import com.mercadolibre.melidata.experiments.model.Experiment;

public class VariantContainer {

  private final Experiment experiment;
  private final String variantId;

  public VariantContainer(final Experiment experiment, final String variantId) {
    this.experiment = experiment;
    this.variantId = variantId;
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public String getVariantId() {
    return variantId;
  }
}
