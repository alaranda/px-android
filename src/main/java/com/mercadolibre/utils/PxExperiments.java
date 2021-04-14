package com.mercadolibre.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PxExperiments {
  /* DUMMY EXPERIMENT. Remove it when you start experimenting for real! */
  EXAMPLE_DUMMY_EXPERIMENT(
      "999999", "dummyName", Arrays.asList(PxExperimentsVariants.EXAMPLE_DUMMY_VARIANT));

  String id;
  String name;
  List<PxExperimentsVariants> variants;

  public Optional<PxExperimentsVariants> getVariantById(String variantId) {
    return getVariants().stream().filter(variant -> variantId.equals(variant.getId())).findAny();
  }

  public static class Constants {}

  @AllArgsConstructor
  @Getter
  public enum PxExperimentsVariants {
    /* DUMMY EXPERIMENT VARIANT. Remove it when you start experimenting for real! */
    EXAMPLE_DUMMY_VARIANT("91919191", "dummyVariantName");

    String id;
    String name;
  }
}
