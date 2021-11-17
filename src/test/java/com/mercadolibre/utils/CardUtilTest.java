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
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class CardUtilTest {

  private static final String MLA = Site.MLA.getSiteId();

  private static final String MLB = Site.MLB.getSiteId();

  private static final String CUIT = "CUIT";

  private static final String CUIL = "CUIL";

  private static final String DNI = "DNI";

  private static final String CDI = "CDI";

  private static final String RG = "RG";

  private static final String CNH = "CNH";

  private static final String CPF = "CPF";

  private static final String CDI_NUMBER = "988766";

  private static final String DNI_NUMBER = "12345678";

  private static final String RG_NUMBER = "M12345678";

  private static final String CNH_NUMBER = "88888765222";

  private static final String CPF_NUMBER = "01234567890";

  private static final String DOCUMENT_NUMBER_WITH_DNI = "20123456784";

  private static final String DOCUMENT_NUMBER_WITHOUT_DNI = "20147360194";

  private final Card card = mock(Card.class);

  private final Payment payment = mock(Payment.class);

  @Test
  public void testIsThirdPartyCard_cardOwnerIdentificationIsNull_expectTrue() {
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(RG, RG_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            null, mockUserIdentification(userMainIdentification, userOtherIdentifications), MLA));
  }

  @Test
  public void testIsThirdPartyCard_cardOwnerIdentificationTypeIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(null, DNI_NUMBER);
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(RG, RG_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_cardOwnerIdentificationNumberIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, null);
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(RG, RG_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_userMainIdentificationIsNullOtherIdentificationIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification), mockUserIdentification(null, null), MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_userMainIdentificationIsNullOtherIdentificationIsEmpty_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(null, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_userMainIdentificationTypeIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final Identification userMainIdentification = mockIdentification(null, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_userMainIdentificationNumberIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final Identification userMainIdentification = mockIdentification(DNI, null);
    final List<Identification> userOtherIdentifications = new ArrayList<>();

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_userOtherIdentificationsOnlyItemIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(null);

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(null, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_userOtherIdentificationsOnlyItemTypeIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(null, DNI_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(null, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_userOtherIdentificationsOnlyItemNumberIsNull_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(DNI, null));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(null, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_matchByTypeAndNumberFromMainIdentification_expectFalse() {
    final Identification cardOwnerIdentification = mockIdentification(CPF, CPF_NUMBER);
    // match
    final Identification userMainIdentification = mockIdentification(CPF, CPF_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CNH, CNH_NUMBER));
    userOtherIdentifications.add(mockIdentification(RG, RG_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_matchByTypeAndNumberFromOtherIdentifications_expectFalse() {
    final Identification cardOwnerIdentification = mockIdentification(CPF, CPF_NUMBER);
    final Identification userMainIdentification = mockIdentification(RG, RG_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CNH, CNH_NUMBER));
    // match
    userOtherIdentifications.add(mockIdentification(CPF, CPF_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void testIsThirdPartyCard_noMatch_noFallbackMLA_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(CPF, CPF_NUMBER);
    final Identification userMainIdentification = mockIdentification(RG, RG_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CNH, CNH_NUMBER));
    userOtherIdentifications.add(mockIdentification(CPF, "99999999900"));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLB));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsDNI_matchMainWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    // match
    final Identification userMainIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIT, DOCUMENT_NUMBER_WITHOUT_DNI));
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsDNI_matchOthersWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final Identification userMainIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITHOUT_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    // match
    userOtherIdentifications.add(mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI));
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsDNI_noMatchWithFallbackMLA_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(DNI, DNI_NUMBER);
    final Identification userMainIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITHOUT_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIL, DOCUMENT_NUMBER_WITHOUT_DNI));
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCUIL_matchMainWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    // match
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIT, DOCUMENT_NUMBER_WITHOUT_DNI));
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCUIL_matchOthersWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    final Identification userMainIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITH_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));
    // match
    userOtherIdentifications.add(mockIdentification(DNI, DNI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCUIL_noMatchWithFallbackMLA_expectTrue() {
    final Identification cardOwnerIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    final Identification userMainIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITH_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCUIT_matchMainWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITH_DNI);
    // match
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIL, DOCUMENT_NUMBER_WITHOUT_DNI));
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCUIT_matchOthersWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITH_DNI);
    final Identification userMainIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));
    // match
    userOtherIdentifications.add(mockIdentification(DNI, DNI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCUIT_noMatchWithFallbackMLA_expectTrue() {
    final Identification cardOwnerIdentification =
        mockIdentification(CUIT, DOCUMENT_NUMBER_WITH_DNI);
    final Identification userMainIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CDI, CDI_NUMBER));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCDI_matchMainWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification =
        mockIdentification(CDI, DOCUMENT_NUMBER_WITH_DNI);
    // match
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIL, DOCUMENT_NUMBER_WITHOUT_DNI));
    userOtherIdentifications.add(mockIdentification(CUIT, DOCUMENT_NUMBER_WITHOUT_DNI));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCDI_matchOthersWithFallbackMLA_expectFalse() {
    final Identification cardOwnerIdentification =
        mockIdentification(CDI, DOCUMENT_NUMBER_WITH_DNI);
    final Identification userMainIdentification =
        mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIT, DOCUMENT_NUMBER_WITHOUT_DNI));
    // match
    userOtherIdentifications.add(mockIdentification(DNI, DNI_NUMBER));

    assertFalse(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationIsCDI_noMatchWithFallbackMLA_expectTrue() {
    final Identification cardOwnerIdentification = mockIdentification(CDI, CDI_NUMBER);
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIL, DOCUMENT_NUMBER_WITHOUT_DNI));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  @Test
  public void
      testIsThirdPartyCard_cardOwnerIdentificationOther_noMatchWithFallbackMLA_expectTrue() {
    final Identification cardOwnerIdentification =
        mockIdentification("FAKE", DOCUMENT_NUMBER_WITH_DNI);
    final Identification userMainIdentification = mockIdentification(DNI, DNI_NUMBER);
    final List<Identification> userOtherIdentifications = new ArrayList<>();
    userOtherIdentifications.add(mockIdentification(CUIL, DOCUMENT_NUMBER_WITH_DNI));
    userOtherIdentifications.add(mockIdentification(CUIT, DOCUMENT_NUMBER_WITH_DNI));

    assertTrue(
        CardUtil.isThirdPartyCard(
            mockCardholder(cardOwnerIdentification),
            mockUserIdentification(userMainIdentification, userOtherIdentifications),
            MLA));
  }

  private Cardholder mockCardholder(final Identification identification) {
    final Cardholder cardholder = mock(Cardholder.class);
    when(cardholder.getIdentification()).thenReturn(identification);
    return cardholder;
  }

  private UserIdentification mockUserIdentification(
      final Identification mainIdentification, final List<Identification> otherIdentifications) {
    final UserIdentification user = mock(UserIdentification.class);
    when(user.getIdentification()).thenReturn(mainIdentification);
    when(user.getPersonOtherIdentifications()).thenReturn(otherIdentifications);
    return user;
  }

  private Identification mockIdentification(final String type, final String number) {
    final Identification identification = mock(Identification.class);
    when(identification.getType()).thenReturn(type);
    when(identification.getNumber()).thenReturn(number);

    return identification;
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenNotMLA() {
    assertFalse(CardUtil.isCardPaymentFromMla(Site.MLB.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenPaymentIsNull() {
    assertFalse(CardUtil.isCardPaymentFromMla(Site.MLA.getSiteId(), null));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenPaymentTypeNotCard() {
    when(payment.getPaymentTypeId()).thenReturn("prepaid_card");
    assertFalse(CardUtil.isCardPaymentFromMla(Site.MLA.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_false_whenPaymentCardIsNull() {
    when(payment.getPaymentTypeId()).thenReturn("credit_card");
    when(payment.getCard()).thenReturn(null);
    assertFalse(CardUtil.isCardPaymentFromMla(Site.MLA.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_true_whenPaymentTypeCreditCard() {
    when(payment.getPaymentTypeId()).thenReturn("credit_card");
    when(payment.getCard()).thenReturn(card);
    assertTrue(CardUtil.isCardPaymentFromMla(Site.MLA.getSiteId(), payment));
  }

  @Test
  public void test_isCardPaymentFromMLA_true_whenPaymentTypeDebitCard() {
    when(payment.getPaymentTypeId()).thenReturn("debit_card");
    when(payment.getCard()).thenReturn(card);
    assertTrue(CardUtil.isCardPaymentFromMla(Site.MLA.getSiteId(), payment));
  }
}
