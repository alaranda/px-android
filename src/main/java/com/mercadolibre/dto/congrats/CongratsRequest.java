package com.mercadolibre.dto.congrats;

import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public class CongratsRequest {

  private String userId;
  private String clientId;
  private String siteId;
  private String paymentIds;
  private String platform;
  private UserAgent userAgent;
  private String density;
  private String productId;
  private String campaignId;
  private String flowName;
  private boolean ifpe;
  private String paymentMethodsIds;
  private String preferenceId;
}
