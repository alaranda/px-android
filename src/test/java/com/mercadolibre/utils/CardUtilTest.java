package com.mercadolibre.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.kyc.UserIdentification;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.px.dto.lib.card.Cardholder;
import com.mercadolibre.px.dto.lib.card.v4.Card;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.user.Identification;
import org.junit.Before;
import org.junit.Test;

public class CardUtilTest {

  private Card card = mock(Card.class);
  private UserIdentification user = mock(UserIdentification.class);
  private Cardholder cardholder = mock(Cardholder.class);
  private Payment payment = mock(Payment.class);

  private Identification cardIdentification = new Identification("12345678", "DNI");
  private Identification userIdentification = new Identification("12345678", "DNI");

  @Before
  public void setUp() throws Exception {
    when(user.getIdentification()).thenReturn(userIdentification);
    when(card.getCardholder()).thenReturn(cardholder);
    when(cardholder.getIdentification()).thenReturn(cardIdentification);
  }

  @Test
  public void test_isThirdPartyCard_userIdentification_isNull() {
    when(card.getCardholder()).thenReturn(cardholder);
    assertFalse(CardUtil.isThirdPartyCard(null, cardholder));
  }

  @Test
  public void test_isThirdPartyCard_cardholder_isNull() {
    assertFalse(CardUtil.isThirdPartyCard(new Identification("24123456780", "CUIT"), null));
  }

  @Test
  public void test_isThirdPartyCard_whenFalse() {
    assertFalse(CardUtil.isThirdPartyCard(user.getIdentification(), cardholder));
  }

  @Test
  public void test_isThirdPartyCard_whenFalse_castCuit() {
    when(user.getIdentification()).thenReturn(new Identification("24123456780", "CUIT"));
    assertFalse(CardUtil.isThirdPartyCard(user.getIdentification(), cardholder));
  }

  @Test
  public void test_isThirdPartyCard_whenFalse_castCuil() {
    when(user.getIdentification()).thenReturn(new Identification("24123456780", "CUIL"));
    assertFalse(CardUtil.isThirdPartyCard(user.getIdentification(), cardholder));
  }

  @Test
  public void test_isThirdPartyCard_whenTrue() {
    when(user.getIdentification()).thenReturn(new Identification("99999999", "DNI"));
    assertTrue(CardUtil.isThirdPartyCard(user.getIdentification(), cardholder));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenNotMLA() {
    assertFalse(CardUtil.isCardPaymentFromMLA(Site.MLB.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenPaymentIsNull() {
    assertFalse(CardUtil.isCardPaymentFromMLA(Site.MLA.getSiteId(), null));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenPaymentTypeNotCard() {
    when(payment.getPaymentTypeId()).thenReturn("prepaid_card");
    assertFalse(CardUtil.isCardPaymentFromMLA(Site.MLA.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenPaymentCardIsNull() {
    when(payment.getPaymentTypeId()).thenReturn("credit_card");
    when(payment.getCard()).thenReturn(null);
    assertFalse(CardUtil.isCardPaymentFromMLA(Site.MLA.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_true_whenPaymentTypeCreditCard() {
    when(payment.getPaymentTypeId()).thenReturn("credit_card");
    when(payment.getCard()).thenReturn(card);
    assertTrue(CardUtil.isCardPaymentFromMLA(Site.MLA.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_true_whenPaymentTypeDebitCard() {
    when(payment.getPaymentTypeId()).thenReturn("debit_card");
    when(payment.getCard()).thenReturn(card);
    assertTrue(CardUtil.isCardPaymentFromMLA(Site.MLA.getSiteId(), payment));
  }
}
