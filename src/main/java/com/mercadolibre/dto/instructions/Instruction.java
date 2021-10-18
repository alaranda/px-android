package com.mercadolibre.dto.instructions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

@Getter
public abstract class Instruction implements InstructionPrototype {

  protected static final String SEPARATOR = " ";
  private static final String SPLIT_NUMBER = "NUMBER";
  private static final String SPLIT_REGEX = "(?<=\\G.{" + SPLIT_NUMBER + "})";

  private String title;
  protected String subtitle;
  private String type;
  protected String accreditationMessage;

  protected List<String> info;
  protected List<String> secondaryInfo;
  protected List<String> tertiaryInfo;

  protected List<Interaction> interactions;
  protected List<Action> actions;
  protected List<Reference> references;

  @Override
  public void createTitle(final String... fields) {
    this.title =
        Arrays.stream(fields).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
  }

  @Override
  public Instruction create(final InstructionMold mold) {
    this.type = mold.getPaymentType().getType();
    this.accreditationMessage = mold.getAccreditationMessage();
    return this;
  }

  @Override
  public void createReferences(final InstructionMold mold) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void createActions(final InstructionMold mold) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void createInteractions(final InstructionMold mold) {
    throw new UnsupportedOperationException();
  }

  protected List<String> splitText(final String text, final int number) {
    return Arrays.asList(text.split(SPLIT_REGEX.replace(SPLIT_NUMBER, String.valueOf(number))));
  }
}
