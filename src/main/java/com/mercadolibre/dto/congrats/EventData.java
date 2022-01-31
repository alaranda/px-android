package com.mercadolibre.dto.congrats;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventData {

  private String audience;
  private String componentId;
  private String contentSource;
  private String printId;
  private String logic;
  private int position;
  private int flow;
  private int campaign_id;



}
