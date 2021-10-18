package com.mercadolibre.dto.instructions;

public interface InstructionPrototype {

  default boolean hasAmount() {
    return Boolean.TRUE;
  }

  default boolean hasAccreditationMessage() {
    return Boolean.TRUE;
  }

  default boolean hasCompany() {
    return Boolean.FALSE;
  }

  Instruction create(final InstructionMold mold);

  void createTitle(final String... fields);

  void createReferences(final InstructionMold mold);

  void createActions(final InstructionMold mold);

  void createInteractions(final InstructionMold mold);
}
