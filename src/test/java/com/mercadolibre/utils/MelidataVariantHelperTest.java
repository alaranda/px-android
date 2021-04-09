package com.mercadolibre.utils;

import static com.mercadolibre.utils.MelidataVariantHelper.MELIDATA_DEFAULT_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.melidata.VariantContainer;
import com.mercadolibre.melidata.MelidataService;
import com.mercadolibre.melidata.experiments.ExperimentConfiguration;
import com.mercadolibre.melidata.experiments.services.ExperimentService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    this.melidataVariantHelper = spy(new MelidataVariantHelper(this.melidataService));
  }

  @Test
  public void testGetVariantsBySeed_success() {
    String mockSeed = "1234567890";
    String defaultMockSeed = "0987654321";

    String mockExperimentId = "111";
    String mockExperimentName = "222";

    String mockExperimentVariantId = "333";
    String mockExperimentVariantName = "444";

    PxExperiments.PxExperimentsVariants mockPxExperimentVariant =
        mock(PxExperiments.PxExperimentsVariants.class);
    when(mockPxExperimentVariant.getId()).thenReturn(mockExperimentVariantId);
    when(mockPxExperimentVariant.getName()).thenReturn(mockExperimentVariantName);

    PxExperiments.PxExperimentsVariants mockPxDefaultExperimentVariant =
        mock(PxExperiments.PxExperimentsVariants.class);
    when(mockPxDefaultExperimentVariant.getId()).thenReturn(MELIDATA_DEFAULT_KEY);
    when(mockPxDefaultExperimentVariant.getName()).thenReturn(MELIDATA_DEFAULT_KEY);

    PxExperiments mockPxExperiment = mock(PxExperiments.class);
    when(mockPxExperiment.getId()).thenReturn(mockExperimentId);
    when(mockPxExperiment.getName()).thenReturn(mockExperimentName);
    when(mockPxExperiment.getVariants())
        .thenReturn(Arrays.asList(mockPxExperimentVariant, mockPxDefaultExperimentVariant));

    when(mockPxExperiment.getVariantById(anyString()))
        .thenReturn(Optional.of(mockPxExperimentVariant));

    // Normal experiment configuration
    ExperimentConfiguration experimentConfiguration = mock(ExperimentConfiguration.class);
    when(experimentConfiguration.getVariantId()).thenReturn(mockExperimentVariantId);
    when(experimentService.getConfig(anyString(), eq(mockSeed)))
        .thenReturn(experimentConfiguration);

    // Default melidata experiment configuration
    ExperimentConfiguration defaultMelidataExperimentConfiguration =
        mock(ExperimentConfiguration.class);
    when(defaultMelidataExperimentConfiguration.getVariantId()).thenReturn(MELIDATA_DEFAULT_KEY);
    when(experimentService.getConfig(anyString(), eq(defaultMockSeed)))
        .thenReturn(defaultMelidataExperimentConfiguration);

    when(melidataVariantHelper.getExperiments()).thenReturn(Arrays.asList(mockPxExperiment));
    final List<VariantContainer> variantContainerList =
        melidataVariantHelper.getVariantsBySeed(mockSeed);
    assertEquals(1, variantContainerList.size());

    final List<VariantContainer> emptyVariantContainerList =
        melidataVariantHelper.getVariantsBySeed(defaultMockSeed);
    assertEquals(0, emptyVariantContainerList.size());
  }

  @Test
  public void testGetVariant_emptyPayerId_emptyVariantList() {

    final List<VariantContainer> variantContainerList = melidataVariantHelper.getVariantsBySeed("");
    assertTrue(variantContainerList.isEmpty());
  }
}
