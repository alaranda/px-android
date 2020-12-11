package com.mercadolibre.dto.payment;

public class BankInfo {

  private UserBankInfo collector;
  private UserBankInfo payer;

  public BankInfo() {}

  public BankInfo(Long mpAccountId, String mpLongName) {
    collector = new UserBankInfo(mpAccountId, mpLongName);
    payer = new UserBankInfo(mpAccountId, mpLongName);
  }

  public static final class UserBankInfo {

    private Long accountId;
    private String longName;

    public UserBankInfo() {}

    public UserBankInfo(Long accountId, String longName) {
      this.accountId = accountId;
      this.longName = longName;
    }

    public Long getAccountId() {
      return accountId;
    }

    public String getLongName() {
      return longName;
    }
  }
}
