package com.mercadolibre.dto.payment;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentPreference {

  final String id;
  final Map<String, Object> internalMetadata;
}
