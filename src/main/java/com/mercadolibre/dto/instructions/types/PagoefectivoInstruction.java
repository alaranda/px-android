package com.mercadolibre.dto.instructions.types;

import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BANBIF_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BBVA_CONTINENTAL_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BBVA_CONTINENTAL_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BCP_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.BCP_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.FULL_CARGA_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.INTERBANK_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.INTERBANK_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.KASNET_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.PAGOEFECTIVO_ATM;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.SCOTIABANK_BANK_TRANSFER;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.SCOTIABANK_TICKET;
import static com.mercadolibre.px.dto.lib.payment.PxPaymentType.WESTERN_UNION_TICKET;

import com.google.common.collect.ImmutableList;
import com.mercadolibre.dto.instructions.Action;
import com.mercadolibre.dto.instructions.ActionTag;
import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionMold;
import com.mercadolibre.dto.instructions.Reference;
import com.mercadolibre.px.dto.lib.payment.PxPaymentType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagoefectivoInstruction extends Instruction {

  private static final Map<PxPaymentType, String> TITLE_PREFIXES;
  private static final Map<PxPaymentType, String> TITLE_SUFFIXES;
  private static final String SUBTITLE = "Paga con estos datos";

  private static final Map<PxPaymentType, List<String>> INFO_TEXTS;
  private static final String TERTIARY_INFO_TEXT_1 = "Ya te enviamos todos los datos a tu e-mail.";
  private static final String TERTIARY_INFO_TEXT_2 =
      "Recuerda pagar antes de los 7 días generado este código.";

  private static final Map<PxPaymentType, List<Action>> ACTIONS;
  private static final String REFERENCE_LABEL = "Digita el código:";
  private static final String UPPERCASE_REFERENCE_LABEL = "DIGITA EL CÓDIGO";

  static {
    TITLE_PREFIXES = new HashMap<>();
    TITLE_PREFIXES.put(BANBIF_BANK_TRANSFER, "Paga");
    TITLE_PREFIXES.put(BBVA_CONTINENTAL_BANK_TRANSFER, "Paga");
    TITLE_PREFIXES.put(BCP_BANK_TRANSFER, "Paga");
    TITLE_PREFIXES.put(INTERBANK_BANK_TRANSFER, "Paga");
    TITLE_PREFIXES.put(SCOTIABANK_BANK_TRANSFER, "Paga");
    TITLE_PREFIXES.put(KASNET_TICKET, "Ahora ve a un agente Kasnet y paga");
    TITLE_PREFIXES.put(WESTERN_UNION_TICKET, "Paga");
    TITLE_PREFIXES.put(BBVA_CONTINENTAL_TICKET, "Ahora ve a un agente BBVA Continental y paga");
    TITLE_PREFIXES.put(BCP_TICKET, "Ahora ve a un agente BCP y paga");
    TITLE_PREFIXES.put(INTERBANK_TICKET, "Ahora ve a un agente Interbank y paga");
    TITLE_PREFIXES.put(SCOTIABANK_TICKET, "Ahora ve a un agente Scotiabank y paga");
    TITLE_PREFIXES.put(PAGOEFECTIVO_ATM, "Ahora ve a un agente de PagoEfectivo y paga");
    TITLE_PREFIXES.put(FULL_CARGA_TICKET, "Ahora ve a un agente de Full Carga y paga");
  }

  static {
    TITLE_SUFFIXES = new HashMap<>();
    TITLE_SUFFIXES.put(BANBIF_BANK_TRANSFER, "desde tu banca por internet de BanBif");
    TITLE_SUFFIXES.put(
        BBVA_CONTINENTAL_BANK_TRANSFER, "desde tu banca por internet de BBVA Continental");
    TITLE_SUFFIXES.put(BCP_BANK_TRANSFER, "desde tu banca por internet de BCP");
    TITLE_SUFFIXES.put(INTERBANK_BANK_TRANSFER, "desde tu banca por internet de Interbank");
    TITLE_SUFFIXES.put(SCOTIABANK_BANK_TRANSFER, "desde tu banca por internet de Scotiabank");
    TITLE_SUFFIXES.put(WESTERN_UNION_TICKET, "en un agente Western Union de pago de servicios");
  }

  static {
    INFO_TEXTS = new HashMap<>();
    INFO_TEXTS.put(
        BANBIF_BANK_TRANSFER,
        Collections.singletonList("En ‘Pago de servicios’ busca la empresa PagoEfectivo."));
    INFO_TEXTS.put(
        BBVA_CONTINENTAL_BANK_TRANSFER,
        Collections.singletonList(
            "En ‘Pago de servicios’ busca y elige la empresa ‘PagoEfectivo MN’."));
    INFO_TEXTS.put(
        BCP_BANK_TRANSFER,
        Collections.singletonList(
            "Elige Pago de servicios y luego Empresas diversas. Allí busca la empresa PagoEfectivo."));
    INFO_TEXTS.put(
        INTERBANK_BANK_TRANSFER,
        Collections.singletonList(
            "Elige Pago de servicios y luego Diversas Empresas. En el menú empresas encontrarás PagoEfectivo"));
    INFO_TEXTS.put(
        SCOTIABANK_BANK_TRANSFER,
        Collections.singletonList(
            "Elige Pagos -  Otras instituciones y luego ‘Otros’. Encontrarás la institución PagoEfectivo[Soles].."));
    INFO_TEXTS.put(
        KASNET_TICKET,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: "));
    INFO_TEXTS.put(
        WESTERN_UNION_TICKET,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: "));
    INFO_TEXTS.put(
        BBVA_CONTINENTAL_TICKET,
        Collections.singletonList(
            "Dile al cajero que harás un pago de recaudo de servicios a PagoEfectivo y díctale este código: "));
    INFO_TEXTS.put(
        BCP_TICKET,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo (código 02186) y díctale este número: "));
    INFO_TEXTS.put(
        INTERBANK_TICKET,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo (código 2735001) y díctale este número: "));
    INFO_TEXTS.put(
        SCOTIABANK_TICKET,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: "));
    INFO_TEXTS.put(
        PAGOEFECTIVO_ATM,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: "));
    INFO_TEXTS.put(
        FULL_CARGA_TICKET,
        Collections.singletonList(
            "Dile al cajero que es un pago a la empresa PagoEfectivo y díctale este código: "));
  }

  static {
    ACTIONS = new HashMap<>();
    ACTIONS.put(
        BANBIF_BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca por internet")
                .tag(ActionTag.LINK)
                .url("https://www.bifnet.com.pe/DIBS_BIFNET/pages/s/login.html")
                .build()));
    ACTIONS.put(
        BBVA_CONTINENTAL_BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca por internet")
                .tag(ActionTag.LINK)
                .url("https://www.bbvacontinental.pe/personas/canales/banca-por-internet/")
                .build()));
    ACTIONS.put(
        BCP_BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca por internet")
                .tag(ActionTag.LINK)
                .url("https://www.viabcp.com/wps/portal/viabcpp/personas")
                .build()));
    ACTIONS.put(
        INTERBANK_BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca por internet")
                .tag(ActionTag.LINK)
                .url("http://www.interbank.com.pe")
                .build()));
    ACTIONS.put(
        SCOTIABANK_BANK_TRANSFER,
        Collections.singletonList(
            Action.builder()
                .label("Ir a banca por internet")
                .tag(ActionTag.LINK)
                .url("http://www.scotiabank.com.pe/Personas/Default")
                .build()));
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    super.create(mold);
    super.createTitle(
        TITLE_PREFIXES.get(mold.getPaymentType()),
        mold.getAmount(),
        TITLE_SUFFIXES.get(mold.getPaymentType()));
    this.subtitle = SUBTITLE;
    this.info = INFO_TEXTS.get(mold.getPaymentType());
    this.tertiaryInfo = ImmutableList.of(TERTIARY_INFO_TEXT_1, TERTIARY_INFO_TEXT_2);
    this.createActions(mold);
    this.createReferences(mold);
    return this;
  }

  @Override
  public void createActions(final InstructionMold mold) {
    this.actions = ACTIONS.get(mold.getPaymentType());
  }

  @Override
  public void createReferences(final InstructionMold mold) {

    final Reference.ReferenceBuilder referenceBuilder =
        Reference.builder().fieldValue(Collections.singletonList(mold.getTransactionId()));

    switch (mold.getPaymentType()) {
      case BANBIF_BANK_TRANSFER:
      case BBVA_CONTINENTAL_BANK_TRANSFER:
      case BCP_BANK_TRANSFER:
        this.references =
            Collections.singletonList(referenceBuilder.label(UPPERCASE_REFERENCE_LABEL).build());
        break;
      case INTERBANK_BANK_TRANSFER:
      case SCOTIABANK_BANK_TRANSFER:
        this.references =
            Collections.singletonList(referenceBuilder.label(REFERENCE_LABEL).build());
        break;
      case KASNET_TICKET:
      case WESTERN_UNION_TICKET:
      case BBVA_CONTINENTAL_TICKET:
      case BCP_TICKET:
      case INTERBANK_TICKET:
      case SCOTIABANK_TICKET:
      case PAGOEFECTIVO_ATM:
      case FULL_CARGA_TICKET:
        this.references = Collections.singletonList(referenceBuilder.build());
    }
  }
}
