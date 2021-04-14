package com.mercadolibre.utils;

import com.mercadolibre.dto.melidata.VariantContainer;
import com.mercadolibre.melidata.MelidataService;
import com.mercadolibre.melidata.experiments.ExperimentConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MelidataVariantHelper {

  public static final String MELIDATA_DEFAULT_KEY = "default";

  private MelidataService melidataService;

  public MelidataVariantHelper(final MelidataService melidataService) {
    this.melidataService = melidataService;
    this.melidataService.getExperimentService().refreshCacheSync();
  }

  public Collection<PxExperiments> getExperiments() {
    return Arrays.asList(PxExperiments.values());
  }

  public List<VariantContainer> getVariantsBySeed(final String experimentSeed) {
    final List<VariantContainer> variantContainerList = new ArrayList<>();

    if (StringUtils.isBlank(experimentSeed)) {
      return variantContainerList;
    }

    for (PxExperiments experiment : getExperiments()) {
      String experimentName = experiment.getName();

      ExperimentConfiguration experimentConfiguration =
          melidataService.getExperimentService().getConfig(experimentName, experimentSeed);

      if (experimentConfiguration != null
          && !MELIDATA_DEFAULT_KEY.equalsIgnoreCase(experimentConfiguration.getVariantId())) {

        VariantContainer variantContainer =
            new VariantContainer(
                experiment.getId(),
                experimentName,
                experimentConfiguration.getVariantId(),
                experiment.getVariantById(experimentConfiguration.getVariantId()).get().getName(),
                experimentConfiguration);
        variantContainerList.add(variantContainer);
      }
    }

    return variantContainerList;
  }
}
