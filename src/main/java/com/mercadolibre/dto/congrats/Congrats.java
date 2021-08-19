package com.mercadolibre.dto.congrats;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.px.dto.lib.button.Button;
import com.mercadolibre.px.dto.lib.text.Text;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Congrats {

  private Points mpuntos;
  private Discounts discounts;
  private Set<CrossSelling> crossSelling;
  private Action viewReceipt;
  private Text topTextBox;
  @Builder.Default private boolean customOrder = false;
  private ExpenseSplit expenseSplit;
  private Map<String, String> paymentMethodsImages;
  private Button primaryButton;
  private Button secondaryButton;
  private String backUrl;
  private String redirectUrl;
  private AutoReturn autoReturn;
  private Instruction instructions;
  private OperationInfo operationInfo;

  public Congrats() {
    this.mpuntos = null;
    this.discounts = null;
    this.crossSelling = null;
    this.viewReceipt = null;
    this.topTextBox = null;
  }

  public boolean hasPoints() {
    return null != this.mpuntos ? true : false;
  }

  public boolean hasDiscounts() {
    return null != this.discounts ? true : false;
  }

  public Discounts getDiscounts() {
    return discounts;
  }

  public Set<CrossSelling> getCrossSelling() {
    return crossSelling;
  }

  public Points getMpuntos() {
    return mpuntos;
  }

  public Action getViewReceipt() {
    return viewReceipt;
  }

  public Text getTopTextBox() {
    return topTextBox;
  }

  public Boolean getCustomOrder() {
    return customOrder;
  }

  public ExpenseSplit getExpenseSplit() {
    return expenseSplit;
  }

  public Map<String, String> getPaymentMethodsImages() {
    return paymentMethodsImages;
  }

  public Button getPrimaryButton() {
    return primaryButton;
  }

  public Button getSecondaryButton() {
    return secondaryButton;
  }

  public String getBackUrl() {
    return backUrl;
  }

  public AutoReturn getAutoReturn() {
    return autoReturn;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public Instruction getInstructions() {
    return instructions;
  }

  public OperationInfo getOperationInfo() {
    return operationInfo;
  }

  public String toString() {
    return String.format(
        "Congrats{Points=[%s], Discounts=[%s]}", mpuntos.toString(), discounts.toString());
  }
}
