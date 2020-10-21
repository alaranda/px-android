package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.mockPayerPaymentMethod;
import static com.mercadolibre.helper.MockTestHelper.mockRemediesRequest;
import static com.mercadolibre.service.PreferenceServiceTest.CONTEXT_ES;
import static com.mercadolibre.utils.Translations.REMEDY_OTHER_REASON_MESSAGE;
import static com.mercadolibre.utils.Translations.REMEDY_OTHER_REASON_TITLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockRiskApi;
import com.mercadolibre.api.RiskApi;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseHighRisk;
import com.mercadolibre.px.api.lib.dto.ConfigurationDao;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.service.remedy.RemedyCvv;
import com.mercadolibre.service.remedy.RemedyHighRisk;
import com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class RemedyHighRiskTest {

  RemedyHighRisk remedyHighRisk =
      new RemedyHighRisk(
          new RiskApi(
              new ConfigurationDao(
                  Integer.valueOf(Config.getInt("risk.socket.timeout")),
                  Integer.valueOf(Config.getInt("risk.socket.timeout")),
                  Integer.valueOf(Config.getInt("default.retries")),
                  Integer.valueOf(Config.getInt("default.retry.delay")),
                  Config.getString("risk.url.scheme"),
                  Config.getString("risk.url.host"))),
          new RemedySuggestionPaymentMethod(
              new RemedyCvv(), REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE));

  private static final String CALLER_ID_TEST = "11111";
  private static final UserAgent USER_AGENT_TEST = UserAgent.create("PX/Android/0.0.0");

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void applyRemedy_statusDetailHighRiskKycPatagonia_remedyHighRiskMP() throws IOException {

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

    final RemediesRequest remediesRequest = mockRemediesRequest(123l, "123", Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final Context context =
        Context.builder().requestId("").locale("es-AR").platform(Platform.MP).build();
    final RemediesResponse remediesResponse =
        remedyHighRisk.applyRemedy(context, remediesRequest, new RemediesResponse());

    final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
    assertThat(responseHighRisk.getTitle(), is("Validá tu identidad para realizar el pago"));
    assertThat(
        responseHighRisk.getMessage(),
        is("Te pediremos algunos datos. Tené a mano tu DNI. Solo te llevará unos minutos."));
    assertThat(
        responseHighRisk.getDeepLink(),
        is("mercadopago://kyc/?initiative=px-high-risk&callback=mercadopago://px/one_tap"));
  }

  @Test
  public void applyRemedy_statusDetailHighRiskKycPatagonia_remedyHighRiskML()
      throws IOException, ApiException {

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final Context context =
        Context.builder().requestId("").locale("es-AR").platform(Platform.ML).build();
    final RemediesResponse remediesResponse =
        remedyHighRisk.applyRemedy(context, remediesRequest, new RemediesResponse());

    final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
    assertThat(
        responseHighRisk.getDeepLink(),
        is("meli://kyc/?initiative=px-high-risk&callback=meli://px/one_tap"));
  }

  @Test
  public void applyRemedy_statusDetailHighRiskWithOutKycTag_remedyHighRisk() throws IOException {

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/risk/withoutKyc.json")));

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyHighRisk.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
    assertThat(responseHighRisk, nullValue());
  }

  @Test
  public void applyRemedy_statusDetailHighRiskInvalidSiteKyc_withoutRemedy() throws IOException {

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLM.name());
    when(remediesRequest.isOneTap()).thenReturn(true);
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyHighRisk.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getHighRisk(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod(), nullValue());
  }

  @Test
  public void applyRemedy_statusDetailHighRiskWithoutAccessToken_withoutRemedy()
      throws IOException {

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

    final RemediesRequest remediesRequest = mockRemediesRequest(123l, null, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyHighRisk.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getHighRisk(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod(), nullValue());
  }

  @Test
  public void applyRemedy_riskIdError_withoutRemedy() throws IOException, ApiException {

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/risk/risk_404.json")));

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyHighRisk.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getHighRisk(), nullValue());
  }
}
