package com.mercadolibre.service;

import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.ATM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BALOTO;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BALOTO_472;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BALOTO_SER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANBIF_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANORTE;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BBVA_CONTINENTAL_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BBVA_CONTINENTAL_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BCP_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BCP_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.FULL_CARGA_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.HSBC;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.INTERBANK_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.INTERBANK_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.KASNET_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.OTROS;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.PAGOEFECTIVO_ATM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.SCOTIABANK_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.SCOTIABANK_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.SEVEN_ELEVEN;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.TELECOMM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.WESTERN_UNION_TICKET;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.BANAMEX;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.BANCOMER;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.BOLBRADESCO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.DAVIVIENDA;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.EFECTY;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.OXXO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PAGOEFECTIVO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PAGOFACIL;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PEC;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PIX;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.RAPIPAGO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.SERFIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionFactory;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.px.dto.lib.payment.PxPaymentType;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Before;
import org.junit.Test;

public class InstructionsTest {

  private static final String SPACE_SEPARATOR = " ";
  private static final String AMOUNT = "$ 20";
  private static final String ACCREDITATION_MESSAGE = "ACC. MESSAGE";
  private static final String COMPANY = "COMPANY-123";
  private static final String PAYMENT_CODE = "123";
  private static final String ACTIVATION_URI = "www";
  private static final String TRANSACTION_ID = "2828";
  private static final String PAYMENT_ID = "2828";
  private static final String PAY_IDENTIFICATION_NUMBER = "12.345.678";
  private static final String PAY_IDENTIFICATION_TYPE = "DNI";
  private static final String QR_CODE = "QR";

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void getInstruction_invalidPaymentMethod() {

    try {
      InstructionFactory.getInstruction("pix_fake");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "Payment Method ID not found");
    }
  }

  @Test
  public void getInstruction_invalidMethods() {

    try {
      InstructionFactory.getInstruction(PIX).createActions(this.mockInstructionMold(BANK_TRANSFER));
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNull(e.getMessage());
    }

    try {
      InstructionFactory.getInstruction(PIX)
          .createReferences(this.mockInstructionMold(BANK_TRANSFER));
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNull(e.getMessage());
    }

    try {
      InstructionFactory.getInstruction("baloto")
          .createInteractions(this.mockInstructionMold(BALOTO_472));
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNull(e.getMessage());
    }
  }

  @Test
  public void getInstruction_Davivienda() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(DAVIVIENDA).create(mold);

    assertFalse(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve y paga " + AMOUNT + " en Davivienda");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), "El pago se acreditará en 1 día hábil.");

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Imprime el cupón que te enviamos por e-mail, y acércate a cualquier sucursal");

    assertNull(instruction.getSecondaryInfo());

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertNull(instruction.getReferences());
  }

  @Test
  public void getInstruction_Efecty() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(EFECTY).create(mold);

    assertFalse(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve a Efecty y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), "El pago se acreditará al instante.");

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale los datos al cajero y listo.");

    assertNull(instruction.getSecondaryInfo());

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Dile al cajero estos números:");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), PAYMENT_ID);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
  }

  @Test
  public void getInstruction_Pse() {

    final InstructionMold mold = mockInstructionMold(BANK_TRANSFER);

    final Instruction instruction = InstructionFactory.getInstruction("pse").create(mold);

    assertFalse(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, ahora haz una transferencia de " + AMOUNT + " desde " + COMPANY);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), "El pago se acreditará al instante.");

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "Tienes 20 minutos para hacerlo.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Transferir");
    assertEquals(instruction.getActions().get(0).getUrl(), ACTIVATION_URI);
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertNull(instruction.getReferences());
  }

  @Test
  public void getInstruction_Baloto() {

    InstructionMold mold = mockInstructionMold(BALOTO);

    Instruction instruction = InstructionFactory.getInstruction("baloto").create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve a Via Baloto y paga " + AMOUNT);
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), BALOTO.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos números al cajero:");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "también te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Si lo abres desde tu teléfono, será más fácil dictárselo al cajero.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Código de convenio");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), COMPANY);
    assertNull(instruction.getReferences().get(0).getSeparator());
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia de Pago");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), PAYMENT_ID);
    assertNull(instruction.getReferences().get(1).getSeparator());

    mold = mockInstructionMold(BALOTO_SER);

    instruction = InstructionFactory.getInstruction("baloto").create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve a Soluciones en Red y paga " + AMOUNT);
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), BALOTO_SER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos números al cajero:");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "también te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Si lo abres desde tu teléfono, será más fácil dictárselo al cajero.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Código de convenio");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), COMPANY);
    assertNull(instruction.getReferences().get(0).getSeparator());
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia de Pago");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), PAYMENT_ID);
    assertNull(instruction.getReferences().get(1).getSeparator());

    mold = mockInstructionMold(BALOTO_472);

    instruction = InstructionFactory.getInstruction("baloto").create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve a 4-72 y paga " + AMOUNT);
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), BALOTO_472.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos números al cajero:");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "también te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Si lo abres desde tu teléfono, será más fácil dictárselo al cajero.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Código de convenio");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), COMPANY);
    assertNull(instruction.getReferences().get(0).getSeparator());
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia de Pago");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), PAYMENT_ID);
    assertNull(instruction.getReferences().get(1).getSeparator());
  }

  @Test
  public void getInstruction_PagoFacil() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(PAGOFACIL).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta pagar "
            + AMOUNT
            + " efectivo en Pago Fácil. Recordá que solamente podrás pagar en las sucursales que permanecen abiertas.");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale este código al cajero");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Código");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), PAYMENT_ID);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
  }

  @Test
  public void getInstruction_Rapipago() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(RAPIPAGO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta pagar "
            + AMOUNT
            + " efectivo en Rapipago. Recordá que solamente podrás pagar en las sucursales que permanecen abiertas.");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale este código al cajero");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Código");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), PAYMENT_ID);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
  }

  @Test
  public void getInstruction_Bolbradesco() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(BOLBRADESCO).create(mold);

    assertFalse(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Para concluir, você deve pagar seu boleto de " + AMOUNT);
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(
        instruction.getAccreditationMessage(), "O valor será creditado em 1 ou 2 dias úteis.");

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "Enviamos o boleto por e-mail para que você o tenha disponível quando precisar.");

    assertNull(instruction.getTertiaryInfo());

    assertEquals(instruction.getInteractions().size(), 2);
    assertEquals(
        instruction.getInteractions().get(0).getTitle(),
        "Você pode fazer isso pelo Internet Banking com o seu código.");
    assertEquals(instruction.getInteractions().get(0).getContent(), PAYMENT_CODE);
    assertEquals(instruction.getInteractions().get(0).isShowMultilineContent(), Boolean.TRUE);
    assertEquals(instruction.getInteractions().get(0).getAction().getContent(), PAYMENT_CODE);
    assertEquals(instruction.getInteractions().get(0).getAction().getLabel(), "Copiar código");
    assertNull(instruction.getInteractions().get(0).getAction().getUrl());
    assertEquals(instruction.getInteractions().get(0).getAction().getTag(), ActionTag.COPY);
    assertEquals(
        instruction.getInteractions().get(1).getTitle(),
        "Ou imprimir o boleto para pagá-lo em uma agência bancária.");
    assertNull(instruction.getInteractions().get(1).getContent());
    assertEquals(instruction.getInteractions().get(1).isShowMultilineContent(), Boolean.TRUE);
    assertNull(instruction.getInteractions().get(1).getAction().getContent());
    assertEquals(instruction.getInteractions().get(1).getAction().getLabel(), "Ver boleto");
    assertEquals(instruction.getInteractions().get(1).getAction().getUrl(), ACTIVATION_URI);
    assertEquals(instruction.getInteractions().get(1).getAction().getTag(), ActionTag.LINK);

    assertNull(instruction.getActions());

    assertNull(instruction.getReferences());
  }

  @Test
  public void getInstruction_Pec() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(PEC).create(mold);

    assertFalse(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Agora basta ir a uma lotérica e pagar " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Tenha os seguintes dados à mão:");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(
        instruction.getAccreditationMessage(), "O dinheiro é creditado em menos de uma hora.");

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "Enviamos o boleto por e-mail para que você o tenha disponível quando precisar.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(
        instruction.getReferences().get(0).getLabel(), "Código do convênio com o Mercado Pago");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 3);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), "COMP");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(1), "ANY-");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(2), "123");
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Seu " + PAY_IDENTIFICATION_TYPE);
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(
        instruction.getReferences().get(1).getFieldValue().get(0), PAY_IDENTIFICATION_NUMBER);
    assertNull(instruction.getReferences().get(1).getSeparator());
  }

  @Test
  public void getInstruction_Pix() {

    final InstructionMold mold = mockInstructionMold(BANK_TRANSFER);

    final Instruction instruction = InstructionFactory.getInstruction(PIX).create(mold);

    assertFalse(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Seu código Pix de " + AMOUNT + " já pode ser pago");
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), BANK_TRANSFER.getType());
    assertEquals(
        instruction.getAccreditationMessage(), "O pagamento é sem taxas e aprovado na hora.");

    assertNull(instruction.getInfo());

    assertNull(instruction.getSecondaryInfo());

    assertNull(instruction.getTertiaryInfo());

    assertEquals(instruction.getInteractions().size(), 2);
    assertEquals(
        instruction.getInteractions().get(0).getTitle(), "<b>1.</b> Copie o código abaixo");
    assertEquals(instruction.getInteractions().get(0).getContent(), QR_CODE);
    assertEquals(instruction.getInteractions().get(0).isShowMultilineContent(), Boolean.FALSE);
    assertEquals(instruction.getInteractions().get(0).getAction().getContent(), QR_CODE);
    assertEquals(instruction.getInteractions().get(0).getAction().getLabel(), "Copiar código");
    assertNull(instruction.getInteractions().get(0).getAction().getUrl());
    assertEquals(instruction.getInteractions().get(0).getAction().getTag(), ActionTag.COPY);
    assertEquals(
        instruction.getInteractions().get(1).getTitle(),
        "<b>2.</b> Cole o código em “Pix Copia e Cola” no app que preferir para fazer o pagamento.");
    assertNull(instruction.getInteractions().get(1).getContent());
    assertEquals(instruction.getInteractions().get(1).isShowMultilineContent(), Boolean.TRUE);
    assertNull(instruction.getInteractions().get(1).getAction());

    assertNull(instruction.getActions());

    assertNull(instruction.getReferences());
  }

  @Test
  public void getInstruction_Webpay() {

    final InstructionMold mold = mockInstructionMold(BANK_TRANSFER);

    final Instruction instruction = InstructionFactory.getInstruction("webpay").create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora paga " + AMOUNT + " en WebPay");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Ten a mano tu tarjeta de coordenadas o generador de claves.");

    assertNull(instruction.getSecondaryInfo());

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Pagar");
    assertEquals(instruction.getActions().get(0).getUrl(), ACTIVATION_URI);
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.EXPIRABLE_BUTTON);

    assertNull(instruction.getReferences());
  }

  @Test
  public void getInstruction_Banamex() {

    InstructionMold mold = mockInstructionMold(BANK_TRANSFER);

    Instruction instruction = InstructionFactory.getInstruction(BANAMEX).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta pagar " + AMOUNT + " en tu banca en línea de Banamex");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca en línea");
    assertEquals(instruction.getActions().get(0).getUrl(), "http://www.banamex.com.mx");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 3);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Sucursal");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), "COMPANY");
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Número de cuenta");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), "123");
    assertEquals(instruction.getReferences().get(1).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(2).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(2).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(2).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(2).getSeparator(), SPACE_SEPARATOR);

    mold = mockInstructionMold(ATM);

    instruction = InstructionFactory.getInstruction(BANAMEX).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta depositar " + AMOUNT + " en el Banamex más cercano");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), ATM.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos códigos al cajero");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 3);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Sucursal");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), COMPANY);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Número de cuenta");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 3);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), "COMP");
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(1), "ANY-");
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(2), "123");
    assertEquals(instruction.getReferences().get(1).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(2).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(2).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(2).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(2).getSeparator(), SPACE_SEPARATOR);

    mold = mockInstructionMold(TELECOMM);

    instruction = InstructionFactory.getInstruction(BANAMEX).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta depositar " + AMOUNT + " en el Telecomm más cercano");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TELECOMM.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Indicale al cajero que es un pago de servicios Banamex y te pedirá estos datos");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertEquals(instruction.getTertiaryInfo().size(), 1);
    assertEquals(
        instruction.getTertiaryInfo().get(0),
        "Si pagas un fin de semana o feriado, será al siguiente día hábil.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 3);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Sucursal");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), COMPANY);
    assertEquals(instruction.getReferences().get(0).getSeparator(), "-");
    assertEquals(instruction.getReferences().get(1).getLabel(), "Número de cuenta");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), COMPANY);
    assertEquals(instruction.getReferences().get(1).getSeparator(), "-");
    assertEquals(instruction.getReferences().get(2).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(2).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(2).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(2).getSeparator(), SPACE_SEPARATOR);
  }

  @Test
  public void getInstruction_Bancomer() {

    InstructionMold mold = mockInstructionMold(SEVEN_ELEVEN);

    Instruction instruction = InstructionFactory.getInstruction(BANCOMER).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta depositar " + AMOUNT + " en el 7-Eleven más cercano");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), SEVEN_ELEVEN.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Indicale al cajero que es un pago de servicios BBVA Bancomer y te pedirá estos datos");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Número de convenio CIE");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 3);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), "COMP");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(1), "ANY-");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(2), "123");
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(1).getSeparator(), SPACE_SEPARATOR);

    mold = mockInstructionMold(BANK_TRANSFER);

    instruction = InstructionFactory.getInstruction(BANCOMER).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta pagar " + AMOUNT + " en tu banca en línea de BBVA Bancomer");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Elige pago de servicios a MercadoLibre");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertEquals(instruction.getActions().size(), 1);
    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca en línea");
    assertEquals(instruction.getActions().get(0).getUrl(), "http://www.bancomer.com.mx");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Número de convenio CIE");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 3);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), "COMP");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(1), "ANY-");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(2), "123");
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(1).getSeparator(), SPACE_SEPARATOR);

    mold = mockInstructionMold(ATM);

    instruction = InstructionFactory.getInstruction(BANCOMER).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta depositar " + AMOUNT + " en el BBVA Bancomer más cercano");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), ATM.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos códigos al cajero");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Si hay fila en BBVA Bancomer, no pierdas tiempo.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Paga en el 7-Eleven más cercano con estos mismos datos.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Número de convenio CIE");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 3);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), "COMP");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(1), "ANY-");
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(2), "123");
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(1).getSeparator(), SPACE_SEPARATOR);

    mold = mockInstructionMold(BANORTE);

    instruction = InstructionFactory.getInstruction(BANCOMER).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca en línea de Banorte");
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), BANORTE.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 2);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "¿Prefieres transferir desde tu computadora o tablet?");
    assertEquals(
        instruction.getSecondaryInfo().get(1),
        "Te enviamos un e-mail, para que puedas hacerlo desde tu correo.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertNull(instruction.getReferences());

    mold = mockInstructionMold(HSBC);

    instruction = InstructionFactory.getInstruction(BANCOMER).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca en línea de HSBC");
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), HSBC.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 2);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "¿Prefieres transferir desde tu computadora o tablet?");
    assertEquals(
        instruction.getSecondaryInfo().get(1),
        "Te enviamos un e-mail, para que puedas hacerlo desde tu correo.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertNull(instruction.getReferences());

    mold = mockInstructionMold(OTROS);

    instruction = InstructionFactory.getInstruction(BANCOMER).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca en línea");
    assertNull(instruction.getSubtitle());
    assertEquals(instruction.getType(), OTROS.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertNull(instruction.getInfo());

    assertEquals(instruction.getSecondaryInfo().size(), 2);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "¿Prefieres transferir desde tu computadora o tablet?");
    assertEquals(
        instruction.getSecondaryInfo().get(1),
        "Te enviamos un e-mail, para que puedas hacerlo desde tu correo.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertNull(instruction.getReferences());
  }

  @Test
  public void getInstruction_Oxxo() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction(OXXO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta depositar " + AMOUNT + " en el OXXO más cercano");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos códigos al cajero");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Codigo");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), PAYMENT_CODE);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
  }

  @Test
  public void getInstruction_Serfin() {

    InstructionMold mold = mockInstructionMold(BANK_TRANSFER);

    Instruction instruction = InstructionFactory.getInstruction(SERFIN).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta pagar " + AMOUNT + " en tu banca en línea de Santander");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Elige pago de servicios a MercadoLibre");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca en línea");
    assertEquals(instruction.getActions().get(0).getUrl(), "http://www.santander.com.mx");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);

    mold = mockInstructionMold(ATM);

    instruction = InstructionFactory.getInstruction(SERFIN).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertTrue(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Listo, solo te falta depositar " + AMOUNT + " en el Santander más cercano");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), ATM.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(instruction.getInfo().get(0), "Díctale estos códigos al cajero");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(instruction.getSecondaryInfo().get(0), "También enviamos estos datos a tu email");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 2);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Número de cuenta");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), COMPANY);
    assertEquals(instruction.getReferences().get(0).getSeparator(), SPACE_SEPARATOR);
    assertEquals(instruction.getReferences().get(1).getLabel(), "Referencia");
    assertEquals(instruction.getReferences().get(1).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(1).getFieldValue().get(0), TRANSACTION_ID);
    assertEquals(instruction.getReferences().get(1).getSeparator(), SPACE_SEPARATOR);
  }

  @Test
  public void getInstruction_Abitab() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction("abitab").create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve a un Abitab y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0), "Dile al cajero que pagarás a Mercado Pago y díctale tu CI:");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "Te enviamos los datos por e-mail para que los tengas a mano.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(
        instruction.getReferences().get(0).getFieldValue().get(0), PAY_IDENTIFICATION_NUMBER);
    assertNull(instruction.getReferences().get(0).getSeparator());
  }

  @Test
  public void getInstruction_Redpagos() {

    final InstructionMold mold = mockInstructionMold(TICKET);

    final Instruction instruction = InstructionFactory.getInstruction("redpagos").create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Listo, ahora ve a Redpagos y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0), "Dile al cajero que pagarás a Mercado Pago y díctale tu CI:");

    assertEquals(instruction.getSecondaryInfo().size(), 1);
    assertEquals(
        instruction.getSecondaryInfo().get(0),
        "Te enviamos los datos por e-mail para que los tengas a mano.");

    assertNull(instruction.getTertiaryInfo());

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(
        instruction.getReferences().get(0).getFieldValue().get(0), PAY_IDENTIFICATION_NUMBER);
    assertNull(instruction.getReferences().get(0).getSeparator());
  }

  @Test
  public void getInstruction_Pagoefectivo() {

    InstructionMold mold = mockInstructionMold(BANBIF_BANK_TRANSFER);

    Instruction instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca por internet de BanBif");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BANBIF_BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0), "En ‘Pago de servicios’ busca la empresa PagoEfectivo.");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca por internet");
    assertEquals(
        instruction.getActions().get(0).getUrl(),
        "https://www.bifnet.com.pe/DIBS_BIFNET/pages/s/login.html");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "DIGITA EL CÓDIGO");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(BBVA_CONTINENTAL_BANK_TRANSFER);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Paga " + AMOUNT + " desde tu banca por internet de BBVA Continental");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BBVA_CONTINENTAL_BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "En ‘Pago de servicios’ busca y elige la empresa ‘PagoEfectivo MN’.");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca por internet");
    assertEquals(
        instruction.getActions().get(0).getUrl(),
        "https://www.bbvacontinental.pe/personas/canales/banca-por-internet/");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "DIGITA EL CÓDIGO");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(BCP_BANK_TRANSFER);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca por internet de BCP");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BCP_BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Elige Pago de servicios y luego Empresas diversas. Allí busca la empresa PagoEfectivo.");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca por internet");
    assertEquals(
        instruction.getActions().get(0).getUrl(),
        "https://www.viabcp.com/wps/portal/viabcpp/personas");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "DIGITA EL CÓDIGO");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(INTERBANK_BANK_TRANSFER);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca por internet de Interbank");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), INTERBANK_BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Elige Pago de servicios y luego Diversas Empresas. En el menú empresas encontrarás PagoEfectivo");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca por internet");
    assertEquals(instruction.getActions().get(0).getUrl(), "http://www.interbank.com.pe");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Digita el código:");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(SCOTIABANK_BANK_TRANSFER);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(), "Paga " + AMOUNT + " desde tu banca por internet de Scotiabank");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), SCOTIABANK_BANK_TRANSFER.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Elige Pagos -  Otras instituciones y luego ‘Otros’. Encontrarás la institución PagoEfectivo[Soles]..");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions().get(0).getContent());
    assertEquals(instruction.getActions().get(0).getLabel(), "Ir a banca por internet");
    assertEquals(
        instruction.getActions().get(0).getUrl(), "http://www.scotiabank.com.pe/Personas/Default");
    assertEquals(instruction.getActions().get(0).getTag(), ActionTag.LINK);

    assertEquals(instruction.getReferences().size(), 1);
    assertEquals(instruction.getReferences().get(0).getLabel(), "Digita el código:");
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(KASNET_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente Kasnet y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), KASNET_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(WESTERN_UNION_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(
        instruction.getTitle(),
        "Paga " + AMOUNT + " en un agente Western Union de pago de servicios");
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), WESTERN_UNION_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(BBVA_CONTINENTAL_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente BBVA Continental y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BBVA_CONTINENTAL_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que harás un pago de recaudo de servicios a PagoEfectivo y díctale este código: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(BCP_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente BCP y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), BCP_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo (código 02186) y díctale este número: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(INTERBANK_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente Interbank y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), INTERBANK_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo (código 2735001) y díctale este número: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(SCOTIABANK_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente Scotiabank y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), SCOTIABANK_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(PAGOEFECTIVO_ATM);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente de PagoEfectivo y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), PAGOEFECTIVO_ATM.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());

    mold = mockInstructionMold(FULL_CARGA_TICKET);

    instruction = InstructionFactory.getInstruction(PAGOEFECTIVO).create(mold);

    assertTrue(instruction.hasAccreditationMessage());
    assertFalse(instruction.hasCompany());
    assertTrue(instruction.hasAmount());

    assertEquals(instruction.getTitle(), "Ahora ve a un agente de Full Carga y paga " + AMOUNT);
    assertEquals(instruction.getSubtitle(), "Paga con estos datos");
    assertEquals(instruction.getType(), FULL_CARGA_TICKET.getType());
    assertEquals(instruction.getAccreditationMessage(), ACCREDITATION_MESSAGE);

    assertEquals(instruction.getInfo().size(), 1);
    assertEquals(
        instruction.getInfo().get(0),
        "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: ");

    assertNull(instruction.getSecondaryInfo());

    assertEquals(instruction.getTertiaryInfo().size(), 2);
    assertEquals(
        instruction.getTertiaryInfo().get(0), "Ya te enviamos todos los datos a tu e-mail.");
    assertEquals(
        instruction.getTertiaryInfo().get(1),
        "Recuerda pagar antes de los 7 días generado este código.");

    assertNull(instruction.getInteractions());

    assertNull(instruction.getActions());

    assertEquals(instruction.getReferences().size(), 1);
    assertNull(instruction.getReferences().get(0).getLabel());
    assertEquals(instruction.getReferences().get(0).getFieldValue().size(), 1);
    assertEquals(instruction.getReferences().get(0).getFieldValue().get(0), TRANSACTION_ID);
    assertNull(instruction.getReferences().get(0).getSeparator());
  }

  private InstructionMold mockInstructionMold(final PxPaymentType type) {
    return InstructionMold.builder()
        .paymentType(type)
        .amount(AMOUNT)
        .accreditationMessage(ACCREDITATION_MESSAGE)
        .company(COMPANY)
        .paymentCode(PAYMENT_CODE)
        .activationUri(ACTIVATION_URI)
        .transactionId(TRANSACTION_ID)
        .paymentId(PAYMENT_ID)
        .payerIdentificationNumber(PAY_IDENTIFICATION_NUMBER)
        .payerIdentificationType(PAY_IDENTIFICATION_TYPE)
        .qrCode(QR_CODE)
        .build();
  }
}
