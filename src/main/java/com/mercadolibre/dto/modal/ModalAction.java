package com.mercadolibre.dto.modal;

import com.mercadolibre.px.dto.lib.button.Button;
import com.mercadolibre.px.dto.lib.text.Text;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModalAction {
  private Text title;
  private Text description;
  private Button mainButton;
  private Button secondaryButton;
}
