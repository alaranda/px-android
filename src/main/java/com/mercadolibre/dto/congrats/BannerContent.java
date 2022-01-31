package com.mercadolibre.dto.congrats;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BannerContent {

  private String contentId;
  private String markup;
  private String imageUrl;
  private String deeplink;
  private String destinationUrl;
  private String printUrl;



}
