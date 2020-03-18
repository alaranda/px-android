package com.mercadolibre.service;

import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockRiskApi;
import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.ResponseBadFilledDate;
import com.mercadolibre.dto.remedies.ResponseCallForAuth;
import com.mercadolibre.dto.remedies.ResponseCvv;
import com.mercadolibre.dto.remedies.ResponseHighRisk;
import com.mercadolibre.dto.remedies.ResponseRemedyDefault;
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
    private static final String PLATFORM = "MP";

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

        final ResponseCallForAuth responseCallForAuth = remediesResponse.getResponseCallForAuth();
        assertThat(responseCallForAuth.getTitle(), is("Tu visa ICBC **** 4444 no autorizo el pago."));
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

        final ResponseCvv responseCvv = remediesResponse.getResponseCvv();
        assertThat(responseCvv.getTitle(), is("El codigo de seguridad es invalido"));
        assertThat(responseCvv.getMessage(), is("Vuelve a ingresarlo para confirmar el pago con tu Santander **** 5555"));
        assertThat(responseCvv.getFieldSetting().getTitle(), is("Codigo de seguridad"));
        assertThat(responseCvv.getFieldSetting().getHintMessage(), is("Los 3 numeros estan al back de tu tarjeta"));
    }

    @Test
    public void getRemedy_statusDetailBadFilledDateHsbc_remedyBadFilledDate() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_badFilledDate.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "3333", "Hsbc", null, USER_AGENT_TEST,
                null, 0, 0L, null, null);

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, PAYMENT_ID_TEST, remediesRequest);

        final ResponseBadFilledDate responseBadFilledDate = remediesResponse.getResponseBadFilledDate();
        assertThat(responseBadFilledDate.getTitle(), is("El vencimiento es invalido"));
        assertThat(responseBadFilledDate.getMessage(), is("Vuelve a ingresarlo para confirmar el pago con tu Hsbc **** 3333"));
        assertThat(responseBadFilledDate.getFieldSetting().getTitle(), is("Vencimiento"));
        assertThat(responseBadFilledDate.getFieldSetting().getHintMessage(), is("MM/AA"));
    }

    @Test
    public void getRemedy_statusDetailDefaultBbvv_remedyDefault() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_default.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, "credit_card", null, null, null,
                USER_AGENT_TEST, null, 0, 0l, null, null);

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, PAYMENT_ID_TEST, remediesRequest);

        final ResponseRemedyDefault responseRemedyDefault = remediesResponse.getResponseWithOutRemedy();
        assertThat(responseRemedyDefault, nullValue());
    }

    @Test
    public void getRemedy_statusDetailHighRiskKycPatagonia_remedyHighRisk() throws IOException, ApiException {

        MockPaymentAPI.getPayment(PAYMENT_ID_TEST, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

        MockRiskApi.getRisk(123L, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

        final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
        mocks(remediesRequest, null, "2222", "Patagonia", null,
                USER_AGENT_TEST, null, 0, 123L, CALLER_ID_TEST, Site.MLA.name());

        final RemediesResponse remediesResponse = remediesService.getRemedy(CONTEXT_ES, "123456789", remediesRequest);

        final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
        assertThat(responseHighRisk.getTitle(), is("Valida tu identidad para poder realizar el pago."));
        assertThat(responseHighRisk.getMessage(), is("Te pediremos que completes algunos datos y que tengas a mano tu DNI para sacarle una foto. Solo te llevara 2 minutos."));
        assertThat(responseHighRisk.getDeepLink(), is("mercadopago://kyc/?initiative=px-high-risk&callback=mercadopago://example-callback/"));
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
