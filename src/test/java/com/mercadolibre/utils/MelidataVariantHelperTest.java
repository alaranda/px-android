package com.mercadolibre.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.melidata.VariantContainer;
import com.mercadolibre.melidata.MelidataService;
import com.mercadolibre.melidata.experiments.ExperimentConfiguration;
import com.mercadolibre.melidata.experiments.model.Experiment;
import com.mercadolibre.melidata.experiments.model.Variant;
import com.mercadolibre.melidata.experiments.services.ExperimentService;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MelidataVariantHelperTest {

  private ExperimentService experimentService;
  private MelidataService melidataService;
  private MelidataVariantHelper melidataVariantHelper;

  @Before
  public void setUp() {
    this.experimentService = Mockito.mock(ExperimentService.class);
    this.melidataService = Mockito.mock(MelidataService.class);
    when(this.melidataService.getExperimentService()).thenReturn(this.experimentService);
    this.melidataVariantHelper = new MelidataVariantHelper(this.melidataService);
  }

  @Test
  public void testGetVarianUserId_twoVariants_variantA() {

    final Variant variantA = Mockito.mock(Variant.class);
    when(variantA.getName()).thenReturn("variante A");
    when(variantA.getId()).thenReturn(Integer.valueOf(1));
    when(variantA.getConfiguration())
        .thenReturn(
            new HashMap<String, Object>() {
              {
                put("enabled_users", "111");
              }
            });

    final Variant variantB = Mockito.mock(Variant.class);
    when(variantB.getName()).thenReturn("variante B");
    when(variantB.getId()).thenReturn(Integer.valueOf(2));
    when(variantA.getConfiguration())
        .thenReturn(
            new HashMap<String, Object>() {
              {
                put("enabled_users", "222");
              }
            });

    final List<Variant> variants = Arrays.asList(variantA, variantB);

    final Experiment experiment = Mockito.mock(Experiment.class);
    when(experiment.getName()).thenReturn("experimento A");
    when(experiment.getVariants()).thenReturn(variants);

    final Collection<Experiment> experiments = Arrays.asList(experiment);
    when(experimentService.getAllExperiments()).thenReturn(experiments);

    final ExperimentConfiguration experimentConfiguration =
        Mockito.mock(ExperimentConfiguration.class);
    when(experimentConfiguration.getVariantId()).thenReturn("variant A");
    when(experimentService.getConfig(anyString(), anyString(), any(), any()))
        .thenReturn(experimentConfiguration);

    final List<VariantContainer> variantContainerList =
        melidataVariantHelper.getVariantsByUser("33333");

    assertThat(variantContainerList.get(0).getVariantId(), is("variant A"));
  }

  @Test
  public void testGetVarianUserId_payerIdBlank_emptyVariantList() {

    final List<VariantContainer> variantContainerList = melidataVariantHelper.getVariantsByUser("");
    assertTrue(variantContainerList.isEmpty());
  }
}
