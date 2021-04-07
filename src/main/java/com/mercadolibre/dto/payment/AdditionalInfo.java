package com.mercadolibre.dto.payment;

import com.mercadolibre.dto.Item;
import com.mercadolibre.px.dto.lib.user.Address;
import com.mercadolibre.px.dto.lib.user.Phone;
import java.util.List;

public class AdditionalInfo {

  private List<Item> items;
  private AdditionalInfoPayer AdditionalInfoPayer;

  AdditionalInfo() {}

  public List<Item> getItems() {
    return items;
  }

  public AdditionalInfoPayer getPayer() {
    return AdditionalInfoPayer;
  }

  public static final class AdditionalInfoPayer {
    public String firstName;
    public String lastName;
    public Phone phone;
    public Address address;

    public AdditionalInfoPayer() {}
  }
}
