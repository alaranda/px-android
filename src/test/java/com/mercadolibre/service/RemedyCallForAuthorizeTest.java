package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.mockContextLibDto;
import static com.mercadolibre.helper.MockTestHelper.mockPayerPaymentMethod;
import static com.mercadolibre.helper.MockTestHelper.mockRemediesRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.service.remedy.RemedyCallForAuthorize;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

public class RemedyCallForAuthorizeTest {

  final RemedyCallForAuthorize remedyCallForAuthorize = new RemedyCallForAuthorize();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void applyRemedy_santander_remedyCallForAuthorize() {

    final RemediesRequest remediesRequest = mockRemediesRequest(123l, "123", Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyCallForAuthorize.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());
    assertThat(remediesResponse.getCallForAuth().getTitle(), notNullValue());
    assertThat(remediesResponse.getCallForAuth().getMessage(), notNullValue());
    assertThat(remediesResponse.getCallForAuth().getActionLoud(), notNullValue());
  }
}
