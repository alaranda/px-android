package com.mercadolibre.dto.congrats;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.px.dto.lib.button.Button;
import com.mercadolibre.px.dto.lib.text.Text;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Banner {

  private BannerContent content;
  private EventData eventData;
  private String cId;
  private String cCategory;
  private String experiments;
  private String explain;



}
