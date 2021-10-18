package com.mercadolibre.dto.instructions;

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

import com.mercadolibre.dto.instructions.types.AbitabInstruction;
import com.mercadolibre.dto.instructions.types.BalotoInstruction;
import com.mercadolibre.dto.instructions.types.BanamexInstruction;
import com.mercadolibre.dto.instructions.types.BancomerInstruction;
import com.mercadolibre.dto.instructions.types.BolbradescoInstruction;
import com.mercadolibre.dto.instructions.types.DaviviendaInstruction;
import com.mercadolibre.dto.instructions.types.EfectyInstruction;
import com.mercadolibre.dto.instructions.types.OxxoInstruction;
import com.mercadolibre.dto.instructions.types.PagoefectivoInstruction;
import com.mercadolibre.dto.instructions.types.PagofacilInstruction;
import com.mercadolibre.dto.instructions.types.PecInstruction;
import com.mercadolibre.dto.instructions.types.PixInstruction;
import com.mercadolibre.dto.instructions.types.PseInstruction;
import com.mercadolibre.dto.instructions.types.RapipagoInstruction;
import com.mercadolibre.dto.instructions.types.RedpagosInstruction;
import com.mercadolibre.dto.instructions.types.SerfinInstruction;
import com.mercadolibre.dto.instructions.types.WebpayInstruction;
import com.mercadolibre.utils.datadog.DatadogCongratsMetric;

public class InstructionFactory {

  public static Instruction getInstruction(final String instructionType) {

    switch (instructionType) {
      case DAVIVIENDA:
        return new DaviviendaInstruction();
      case EFECTY:
        return new EfectyInstruction();
      case "pse":
        return new PseInstruction();
      case "baloto":
        return new BalotoInstruction();
      case PAGOFACIL:
        return new PagofacilInstruction();
      case RAPIPAGO:
        return new RapipagoInstruction();
      case BOLBRADESCO:
        return new BolbradescoInstruction();
      case PEC:
        return new PecInstruction();
      case PIX:
        return new PixInstruction();
      case "webpay":
        return new WebpayInstruction();
      case BANAMEX:
        return new BanamexInstruction();
      case BANCOMER:
        return new BancomerInstruction();
      case OXXO:
        return new OxxoInstruction();
      case SERFIN:
        return new SerfinInstruction();
      case "abitab":
        return new AbitabInstruction();
      case "redpagos":
        return new RedpagosInstruction();
      case PAGOEFECTIVO:
        return new PagoefectivoInstruction();
    }
    DatadogCongratsMetric.trackCongratsIllegalPaymentMethod(instructionType);
    throw new IllegalArgumentException("Payment Method ID not found");
  }
}
