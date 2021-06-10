package com.mercadolibre.dto.instructions;

import java.util.List;
import lombok.Getter;

@Getter
public class Instruction {

  private String title;
  private String subtitle;
  private List<String> info;
  private List<String> secondaryInfo;
  private List<String> tertiaryInfo;
  private String accreditationMessage;
  private String type;
  private List<Interaction> interactions;
  private List<String> accreditationComments;
  private List<Action> actions;
  private List<Reference> references;
}
