package com.mercadolibre.dto.congrats;

import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import lombok.*;

@ToString
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
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
  private String merchantOrderId;
  private String merchantAccountId;
  private String accessToken;
  private String publicKey;
  private String paymentTypeId;
}
