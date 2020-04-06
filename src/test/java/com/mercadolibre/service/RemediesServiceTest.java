package com.mercadolibre.service;

import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockRiskApi;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCallForAuth;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.dto.remedy.ResponseHighRisk;
import com.mercadolibre.dto.remedy.ResponseRemedyDefault;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.utils.IOUtils;

import java.io.IOException;
import java.math.BigDecimal;

import static com.mercadolibre.service.PreferenceServiceTest.CONTEXT_ES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

public class RemediesServiceTest {

    private RemediesService remediesService = new RemediesService();

    private static final String PAYMENT_ID_TEST = "123456789";
    private static final String CALLER_ID_TEST = "11111";
    private static final UserAgent USER_AGENT_TEST = UserAgent.create("PX/Android/0.0.0");

    @Before
    public void before() {
        RequestMockHolder.clear();
    }


    @Test
    public void getRemedy_satusDetailCallForAuthIcbc_remedyCallForAuth() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_callForAuth.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, "visa", "4444", "ICBC", new BigDecimal(123),
                USER_AGENT_TEST, null, 0, 0l, null, null);

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, PAYMENT_ID_TEST, remediesRequest);

        final ResponseCallForAuth responseCallForAuth = remediesResponse.getCallForAuth();
        assertThat(responseCallForAuth.getTitle(), is("Tu visa ICBC **** 4444 no autorizo el pago"));
        assertThat(responseCallForAuth.getMessage(), is("Llama a ICBC para autorizar 123 a Mercado Pago o paga de otra forma."));
    }

    @Test
    public void getRemedy_statusDetailCvvSantander_remedyCvv() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_cvv.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "5555", "Santander", new BigDecimal(123),
                USER_AGENT_TEST, "back", 3, 0l, null, null);

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, PAYMENT_ID_TEST, remediesRequest);

        final ResponseCvv responseCvv = remediesResponse.getCvv();
        assertThat(responseCvv.getTitle(), is("El código de seguridad es inválido"));
        assertThat(responseCvv.getMessage(), is("Volvé a ingresarlo para confirmar el pago."));
        assertThat(responseCvv.getFieldSetting().getTitle(), is("Código de seguridad"));
        assertThat(responseCvv.getFieldSetting().getHintMessage(), is("Los 3 números están al dorso de tu tarjeta"));
    }

    @Test
    public void getRemedy_statusDetailDefaultBbvv_remedyDefault() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_default.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, "credit_card", null, null, null,
                USER_AGENT_TEST, null, 0, 0l, null, null);

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, PAYMENT_ID_TEST, remediesRequest);

        final ResponseRemedyDefault responseRemedyDefault = remediesResponse.getWithOutRemedy();
        assertThat(responseRemedyDefault, nullValue());
    }

    @Test
    public void getRemedy_statusDetailHighRiskKycPatagonia_remedyHighRiskMP() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, CALLER_ID_TEST, Site.MLA.name());

        final Context context = Context.builder().requestId("").locale("es-AR").platform(Platform.MP).build();
        final RemediesResponse remediesResponse = remediesService.getRemedy(context, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk.getTitle(), is("Validá tu identidad para realizar el pago"));
        assertThat(responseHighRisk.getMessage(), is("Te pediremos algunos datos. Ten a mano tu DNI. Solo te llevará unos minutos."));
        assertThat(responseHighRisk.getDeepLink(), is("mercadopago://kyc/?initiative=px-high-risk&callback=mercadopago://px/one_tap"));
    }

    @Test
    public void getRemedy_statusDetailHighRiskKycPatagonia_remedyHighRiskML() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, CALLER_ID_TEST, Site.MLA.name());

        final Context context = Context.builder().requestId("").locale("es-AR").platform(Platform.ML).build();
        final RemediesResponse remediesResponse = remediesService.getRemedy(context, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk.getDeepLink(), is("meli://kyc/?initiative=px-high-risk&callback=meli://px/one_tap"));
    }

    @Test
    public void getRemedy_statusDetailHighRiskWithOutKycTag_remedyHighRisk() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/risk/withoutKyc.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, CALLER_ID_TEST, Site.MLA.name());
        when(remediesRequest.getRiskExcecutionId()).thenReturn(123L);
        when(remediesRequest.getUserId()).thenReturn(CALLER_ID_TEST);
        when(remediesRequest.getSiteId()).thenReturn(Site.MLA.name());

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk, nullValue());
    }

    @Test
    public void getRemedy_statusDetailHighRiskInvalidSiteKyc_withoutRemedy() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, CALLER_ID_TEST, Site.MLM.name());

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk, nullValue());
    }

    @Test
    public void getRemedy_statusDetailHighRiskWithoutAccessToken_withoutRemedy() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, null, null);

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk, nullValue());
    }

    @Test
    public void getRemedy_riskIdError_withoutRemedy() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_NOT_FOUND,
                IOUtils.toString(getClass().getResourceAsStream("/risk/risk_404.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, CALLER_ID_TEST, Site.MLA.name());

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk, nullValue());
    }

    private void mocks(final RemediesRequest remediesRequest, final String paymentMethodId, final String lastFourDigit, final String issuerName,
                       final BigDecimal totalAmount, final UserAgent userAgent, final String securityCodeLocation,
                       final int securityCodeLength, final Long riskId, final String callerId, final String site){

        final PayerPaymentMethodRejected payerPaymentMethodRejected = Mockito.mock(PayerPaymentMethodRejected.class);
        when(payerPaymentMethodRejected.getPaymentMethodId()).thenReturn(paymentMethodId);
        when(payerPaymentMethodRejected.getLastFourDigit()).thenReturn(lastFourDigit);
        when(payerPaymentMethodRejected.getIssuerName()).thenReturn(issuerName);
        when(payerPaymentMethodRejected.getTotalAmount()).thenReturn(totalAmount);
        when(payerPaymentMethodRejected.getSecurityCodeLocation()).thenReturn(securityCodeLocation);
        when(payerPaymentMethodRejected.getSecurityCodeLength()).thenReturn(securityCodeLength);
        when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
        when(remediesRequest.getUserAgent()).thenReturn(userAgent);
        when(remediesRequest.getRiskExcecutionId()).thenReturn(riskId);
        when(remediesRequest.getUserId()).thenReturn(callerId);
        when(remediesRequest.getSiteId()).thenReturn(site);
    }
}
