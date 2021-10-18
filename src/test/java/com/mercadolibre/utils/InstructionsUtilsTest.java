package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.BOLBRADESCO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.OXXO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.payment.Barcode;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.TransactionDetails;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.lib.installments.FinancialInstitution;
import com.mercadolibre.px.dto.lib.installments.PaymentMethod;
import com.mercadolibre.px.dto.lib.preference.PointOfInteraction;
import com.mercadolibre.px.dto.lib.site.CurrencyType;
import com.mercadolibre.px.dto.lib.user.Identification;
import com.mercadolibre.px.dto.lib.user.Payer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Locale;
import org.junit.Test;

public class InstructionsUtilsTest {

  @Test
  public void testInstructions_formatAmount() {

    CurrencyType currencyType = mock(CurrencyType.class);

    when(currencyType.getSymbol()).thenReturn("$");
    when(currencyType.getDecimalPlaces()).thenReturn(2);
    when(currencyType.getDecimalSeparator()).thenReturn(',');
    when(currencyType.getThousandsSeparator()).thenReturn('.');

    final String amount1 = InstructionsUtils.formatAmount(BigDecimal.valueOf(20000), currencyType);
    final String amount2 =
        InstructionsUtils.formatAmount(BigDecimal.valueOf(20000.1), currencyType);
    final String amount3 =
        InstructionsUtils.formatAmount(BigDecimal.valueOf(20000.12), currencyType);
    final String amount4 =
        InstructionsUtils.formatAmount(BigDecimal.valueOf(20000.123), currencyType);
    final String amount5 = InstructionsUtils.formatAmount(BigDecimal.valueOf(123), currencyType);

    assertEquals(amount1, "$ 20.000,00");
    assertEquals(amount2, "$ 20.000,10");
    assertEquals(amount3, "$ 20.000,12");
    assertEquals(amount4, "$ 20.000,123");
    assertEquals(amount5, "$ 123,00");
  }

  @Test
  public void testInstructions_getAccreditationMessage() {

    final PaymentMethod paymentMethod = mock(PaymentMethod.class);
    final Locale locale = MockTestHelper.mockContextLibDto().getLocale();

    final String accreditationMessage1 = InstructionsUtils.getAccreditationMessage(locale, null);
    when(paymentMethod.getAccreditationTime()).thenReturn(-1);
    final String accreditationMessage2 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    when(paymentMethod.getAccreditationTime()).thenReturn(0);
    final String accreditationMessage3 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    when(paymentMethod.getAccreditationTime()).thenReturn(1);
    final String accreditationMessage4 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);
    when(paymentMethod.getAccreditationTime()).thenReturn(59);
    final String accreditationMessage5 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);
    when(paymentMethod.getAccreditationTime()).thenReturn(60);
    final String accreditationMessage6 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    when(paymentMethod.getAccreditationTime()).thenReturn(61);
    final String accreditationMessage7 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);
    when(paymentMethod.getAccreditationTime()).thenReturn(1339);
    final String accreditationMessage8 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    when(paymentMethod.getAccreditationTime()).thenReturn(1440);
    final String accreditationMessage9 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);
    when(paymentMethod.getAccreditationTime()).thenReturn(1441);
    final String accreditationMessage10 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);
    when(paymentMethod.getAccreditationTime()).thenReturn(2880);
    final String accreditationMessage11 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    when(paymentMethod.getAccreditationTime()).thenReturn(2881);
    final String accreditationMessage12 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);
    when(paymentMethod.getAccreditationTime()).thenReturn(4321);
    final String accreditationMessage13 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    when(paymentMethod.getAccreditationTime()).thenReturn(null);
    final String accreditationMessage14 =
        InstructionsUtils.getAccreditationMessage(locale, paymentMethod);

    assertEquals(accreditationMessage1, "");
    assertEquals(accreditationMessage2, "");

    assertEquals(accreditationMessage3, "El pago se acreditará al instante.");

    assertEquals(accreditationMessage4, "El pago se acreditará en menos de 1h.");
    assertEquals(accreditationMessage5, "El pago se acreditará en menos de 1h.");
    assertEquals(accreditationMessage6, "El pago se acreditará en menos de 1h.");

    assertEquals(accreditationMessage7, "El pago se acreditará en menos de 2h.");
    assertEquals(accreditationMessage8, "El pago se acreditará en menos de 23h.");

    assertEquals(accreditationMessage9, "El pago se acreditará de 1 a 2 días hábiles.");
    assertEquals(accreditationMessage10, "El pago se acreditará de 1 a 2 días hábiles.");
    assertEquals(accreditationMessage11, "El pago se acreditará de 1 a 2 días hábiles.");

    assertEquals(accreditationMessage12, "El pago se acreditará en 3 días hábiles.");
    assertEquals(accreditationMessage13, "El pago se acreditará en 4 días hábiles.");

    assertEquals(accreditationMessage14, "");
  }

  @Test
  public void testInstructions_getPaymentCode_empty() {

    final Payment payment = mock(Payment.class);

    when(payment.getPaymentMethodId()).thenReturn("");

    final String paymentCode = InstructionsUtils.getPaymentCode(payment);

    assertTrue(paymentCode.isEmpty());
  }

  @Test
  public void testInstructions_getPaymentCode_bolbradesco() {

    final Payment payment = mock(Payment.class);
    final Barcode barcode = mock(Barcode.class);

    when(payment.getPaymentMethodId()).thenReturn(BOLBRADESCO);
    when(barcode.getContent()).thenReturn("23796874800000746173380260985280773600633330");
    when(payment.getBarcode()).thenReturn(barcode);

    final String paymentCode = InstructionsUtils.getPaymentCode(payment);

    when(payment.getBarcode()).thenReturn(null);

    final String empty = InstructionsUtils.getPaymentCode(payment);

    assertEquals(paymentCode, "23793.38029 60985.280779 36006.333300 6 87480000074617");
    assertTrue(empty.isEmpty());
  }

  @Test
  public void testInstructions_getPaymentCode_oxxo() {

    final Payment payment = mock(Payment.class);
    final TransactionDetails transactionDetails = mock(TransactionDetails.class);

    when(payment.getPaymentMethodId()).thenReturn(OXXO);

    when(payment.getTransactionDetails()).thenReturn(transactionDetails);
    when(transactionDetails.getPaymentMethodReferenceId()).thenReturn("9700009852606470");
    final String paymentCode1 = InstructionsUtils.getPaymentCode(payment);

    when(payment.getTransactionDetails()).thenReturn(null);
    final String paymentCode2 = InstructionsUtils.getPaymentCode(payment);

    when(transactionDetails.getPaymentMethodReferenceId()).thenReturn(null);
    when(transactionDetails.getVerificationCode()).thenReturn(null);
    when(payment.getTransactionDetails()).thenReturn(transactionDetails);
    final String paymentCode3 = InstructionsUtils.getPaymentCode(payment);

    when(transactionDetails.getPaymentMethodReferenceId()).thenReturn("123");
    when(transactionDetails.getVerificationCode()).thenReturn("345");
    when(payment.getTransactionDetails()).thenReturn(transactionDetails);
    final String paymentCode4 = InstructionsUtils.getPaymentCode(payment);

    when(transactionDetails.getPaymentMethodReferenceId()).thenReturn(null);
    when(transactionDetails.getVerificationCode()).thenReturn("345");
    when(payment.getTransactionDetails()).thenReturn(transactionDetails);
    final String paymentCode5 = InstructionsUtils.getPaymentCode(payment);

    assertEquals(paymentCode1, "9700009852606470");
    assertTrue(paymentCode2.isEmpty());
    assertTrue(paymentCode3.isEmpty());
    assertEquals(paymentCode4, "345");
    assertEquals(paymentCode5, "345");
  }

  @Test
  public void testInstructions_formatPayerIdentificationNumber_empty() {

    final Identification identification = mock(Identification.class);

    when(identification.getNumber()).thenReturn(null);
    final String number1 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("123");
    when(identification.getType()).thenReturn(null);

    final String number2 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getType()).thenReturn("DNI");
    final String number3 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    assertTrue(number1.isEmpty());
    assertEquals(number2, "123");
    assertEquals(number3, "123");
  }

  @Test
  public void testInstructions_formatPayerIdentificationNumber_cpf() {

    final Identification identification = mock(Identification.class);

    when(identification.getNumber()).thenReturn("123");
    when(identification.getType()).thenReturn("CPF");
    final String number1 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("123456789091");
    final String number2 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("123-45.6789.09");
    final String number3 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("12345678909");
    final String number4 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    assertEquals(number1, "123");
    assertEquals(number2, "123456789091");
    assertEquals(number3, "123.456.789-09");
    assertEquals(number4, "123.456.789-09");
  }

  @Test
  public void testInstructions_formatPayerIdentificationNumber_cnpj() {

    final Identification identification = mock(Identification.class);

    when(identification.getNumber()).thenReturn("123");
    when(identification.getType()).thenReturn("CNPJ");
    final String number1 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("123456789012340");
    final String number2 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("123.456-7890-1234");
    final String number3 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("12345678901234");
    final String number4 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    assertEquals(number1, "123");
    assertEquals(number2, "123456789012340");
    assertEquals(number3, "12.345.678/9012-34");
    assertEquals(number4, "12.345.678/9012-34");
  }

  @Test
  public void testInstructions_formatPayerIdentificationNumber_ci() {

    final Identification identification = mock(Identification.class);

    when(identification.getNumber()).thenReturn("123");
    when(identification.getType()).thenReturn("CI");
    final String number1 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("123456789");
    final String number2 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("12.3456--78");
    final String number3 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("12345678");
    final String number4 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    when(identification.getNumber()).thenReturn("1234567");
    final String number5 = InstructionsUtils.formatPayerIdentificationNumber(identification);

    assertEquals(number1, "123");
    assertEquals(number2, "123456789");
    assertEquals(number3, "1.234.567-8");
    assertEquals(number4, "1.234.567-8");
    assertEquals(number5, "123.456-7");
  }

  @Test
  public void testInstructions_getActivationUri() {

    final TransactionDetails transactionDetails = mock(TransactionDetails.class);

    when(transactionDetails.getExternalResourceUrl()).thenReturn(null);
    final String activationUri1 = InstructionsUtils.getActivationUri(transactionDetails);
    final String activationUri2 = InstructionsUtils.getActivationUri(null);

    when(transactionDetails.getExternalResourceUrl()).thenReturn("abc");

    final String activationUri3 = InstructionsUtils.getActivationUri(transactionDetails);

    assertTrue(activationUri1.isEmpty());
    assertTrue(activationUri2.isEmpty());
    assertEquals(activationUri3, "abc");
  }

  @Test
  public void testInstructions_getTransactionId() {

    final TransactionDetails transactionDetails = mock(TransactionDetails.class);

    when(transactionDetails.getPaymentMethodReferenceId()).thenReturn(null);
    final String transactionId1 = InstructionsUtils.getTransactionId(transactionDetails);
    final String transactionId2 = InstructionsUtils.getTransactionId(null);

    when(transactionDetails.getPaymentMethodReferenceId()).thenReturn("abc");

    final String transactionId3 = InstructionsUtils.getTransactionId(transactionDetails);

    assertTrue(transactionId1.isEmpty());
    assertTrue(transactionId2.isEmpty());
    assertEquals(transactionId3, "abc");
  }

  @Test
  public void testInstructions_getQrCode() {

    final PointOfInteraction pointOfInteraction = mock(PointOfInteraction.class);
    final PointOfInteraction.TransactionData transactionData =
        mock(PointOfInteraction.TransactionData.class);

    when(pointOfInteraction.getTransactionData()).thenReturn(null);
    final String qrCode1 = InstructionsUtils.getQrCode(pointOfInteraction);

    when(pointOfInteraction.getTransactionData()).thenReturn(transactionData);
    final String qrCode2 = InstructionsUtils.getQrCode(pointOfInteraction);

    final String qrCode3 = InstructionsUtils.getQrCode(null);

    when(pointOfInteraction.getTransactionData().getQrCode()).thenReturn("QR");

    final String qrCode4 = InstructionsUtils.getQrCode(pointOfInteraction);

    assertTrue(qrCode1.isEmpty());
    assertTrue(qrCode2.isEmpty());
    assertTrue(qrCode3.isEmpty());
    assertEquals(qrCode4, "QR");
  }

  @Test
  public void testInstructions_getPayerIdentificationNumber() {

    final Payer payer = mock(Payer.class);
    final Identification identification = mock(Identification.class);

    final String payerIdentification1 = InstructionsUtils.getPayerIdentificationNumber(null);

    when(payer.getIdentification()).thenReturn(null);
    final String payerIdentification2 = InstructionsUtils.getPayerIdentificationNumber(payer);

    final String payerIdentification3 = InstructionsUtils.getQrCode(null);

    when(payer.getIdentification()).thenReturn(identification);
    when(payer.getIdentification().getNumber()).thenReturn("123");
    final String payerIdentification4 = InstructionsUtils.getPayerIdentificationNumber(payer);

    assertTrue(payerIdentification1.isEmpty());
    assertTrue(payerIdentification2.isEmpty());
    assertTrue(payerIdentification3.isEmpty());
    assertEquals(payerIdentification4, "123");
  }

  @Test
  public void testInstructions_getPayerIdentificationType() {

    final Payer payer = mock(Payer.class);
    final Identification identification = mock(Identification.class);

    when(payer.getIdentification()).thenReturn(null);
    final String payerIdentificationType1 = InstructionsUtils.getPayerIdentificationType(payer);

    when(payer.getIdentification()).thenReturn(identification);
    final String payerIdentificationType2 = InstructionsUtils.getPayerIdentificationType(payer);

    final String payerIdentificationType3 = InstructionsUtils.getPayerIdentificationType(null);

    when(payer.getIdentification().getType()).thenReturn("CI");

    final String payerIdentificationType4 = InstructionsUtils.getPayerIdentificationType(payer);

    assertTrue(payerIdentificationType1.isEmpty());
    assertTrue(payerIdentificationType2.isEmpty());
    assertTrue(payerIdentificationType3.isEmpty());
    assertEquals(payerIdentificationType4, "CI");
  }

  @Test
  public void testInstructions_getCompany() {

    final PaymentMethod paymentMethod = mock(PaymentMethod.class);
    final Payment payment = mock(Payment.class);
    final TransactionDetails transactionDetails = mock(TransactionDetails.class);
    final FinancialInstitution financialInstitution = mock(FinancialInstitution.class);

    final String company1 = InstructionsUtils.getCompany(null, null);

    when(paymentMethod.getFinancialInstitutions()).thenReturn(null);
    final String company2 = InstructionsUtils.getCompany(paymentMethod, null);

    when(paymentMethod.getFinancialInstitutions())
        .thenReturn(Collections.singletonList(financialInstitution));
    when(payment.getTransactionDetails()).thenReturn(null);
    final String company3 = InstructionsUtils.getCompany(paymentMethod, payment);

    when(paymentMethod.getFinancialInstitutions()).thenReturn(Collections.emptyList());
    when(transactionDetails.getFinancialInstitution()).thenReturn(null);
    when(payment.getTransactionDetails()).thenReturn(transactionDetails);
    final String company4 = InstructionsUtils.getCompany(paymentMethod, payment);

    when(paymentMethod.getFinancialInstitutions()).thenReturn(Collections.emptyList());
    when(transactionDetails.getFinancialInstitution()).thenReturn("abc");
    when(payment.getTransactionDetails()).thenReturn(transactionDetails);
    final String company5 = InstructionsUtils.getCompany(paymentMethod, payment);

    when(financialInstitution.getId()).thenReturn("abc");
    when(paymentMethod.getFinancialInstitutions())
        .thenReturn(Collections.singletonList(financialInstitution));
    when(payment.getTransactionDetails().getFinancialInstitution()).thenReturn(null);
    final String company6 = InstructionsUtils.getCompany(paymentMethod, payment);

    when(financialInstitution.getId()).thenReturn(null);
    when(paymentMethod.getFinancialInstitutions())
        .thenReturn(Collections.singletonList(financialInstitution));
    when(payment.getTransactionDetails().getFinancialInstitution()).thenReturn("abc");
    final String company7 = InstructionsUtils.getCompany(paymentMethod, payment);

    when(financialInstitution.getId()).thenReturn("abc1");
    when(paymentMethod.getFinancialInstitutions())
        .thenReturn(Collections.singletonList(financialInstitution));
    when(payment.getTransactionDetails().getFinancialInstitution()).thenReturn("abc");
    final String company8 = InstructionsUtils.getCompany(paymentMethod, payment);

    when(financialInstitution.getId()).thenReturn("abc");
    when(financialInstitution.getDescription()).thenReturn("descrip");
    when(paymentMethod.getFinancialInstitutions())
        .thenReturn(Collections.singletonList(financialInstitution));
    when(payment.getTransactionDetails().getFinancialInstitution()).thenReturn("abc");
    final String company9 = InstructionsUtils.getCompany(paymentMethod, payment);

    assertTrue(company1.isEmpty());
    assertTrue(company2.isEmpty());
    assertTrue(company3.isEmpty());
    assertTrue(company4.isEmpty());
    assertEquals(company5, "abc");
    assertTrue(company6.isEmpty());
    assertEquals(company7, "abc");
    assertEquals(company8, "abc");
    assertEquals(company9, "descrip");
  }

  @Test
  public void testInstructions_getAmount() {

    final TransactionDetails transactionDetails = mock(TransactionDetails.class);

    CurrencyType currencyType = mock(CurrencyType.class);

    when(currencyType.getSymbol()).thenReturn("$");
    when(currencyType.getDecimalPlaces()).thenReturn(2);
    when(currencyType.getDecimalSeparator()).thenReturn(',');
    when(currencyType.getThousandsSeparator()).thenReturn('.');

    final String amount1 = InstructionsUtils.getAmount(null, currencyType);

    when(transactionDetails.getTotalPaidAmount()).thenReturn(null);
    final String amount2 = InstructionsUtils.getAmount(transactionDetails, currencyType);

    when(transactionDetails.getTotalPaidAmount()).thenReturn(BigDecimal.TEN);
    final String amount3 = InstructionsUtils.getAmount(transactionDetails, currencyType);

    assertTrue(amount1.isEmpty());
    assertTrue(amount2.isEmpty());
    assertEquals(amount3, "$ 10,00");
  }
}
