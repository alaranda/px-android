package com.mercadolibre.utils;

import com.mercadolibre.dto.melidata.VariantContainer;
import com.mercadolibre.melidata.MelidataService;
import com.mercadolibre.melidata.experiments.ExperimentConfiguration;
import com.mercadolibre.melidata.experiments.model.Experiment;
import com.mercadolibre.melidata.experiments.model.Variant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MelidataVariantHelper {

  private static final String MELIDATA_DEFAULT_KEY = "default";

  private MelidataService melidataService;

  public MelidataVariantHelper(final MelidataService melidataService) {
    this.melidataService = melidataService;
    this.melidataService.getExperimentService().refreshCacheSync();
  }

  public List<VariantContainer> getVariantsByUser(final String payerId) {
    final List<VariantContainer> variantContainerList = new ArrayList<>();

    if (StringUtils.isBlank(payerId)) {
      return variantContainerList;
    }

    final Collection<Experiment> activeExperiments =
        melidataService.getExperimentService().getAllExperiments();

    for (Experiment e : activeExperiments) {
      Iterator<Variant> it = e.getVariants().iterator();

      if (!it.hasNext()) {
        continue;
      }

      for (String variantConfig : it.next().getConfiguration().keySet()) {
        ExperimentConfiguration experimentConfiguration =
            melidataService
                .getExperimentService()
                .getConfig(e.getName(), MELIDATA_DEFAULT_KEY, variantConfig, payerId);

        if (MELIDATA_DEFAULT_KEY.equalsIgnoreCase(experimentConfiguration.getVariantId())) {
          continue;
        }

        VariantContainer variantContainer =
            new VariantContainer(e, experimentConfiguration.getVariantId());
        variantContainerList.add(variantContainer);
        break;
      }
    }

    return variantContainerList;
  }
}
