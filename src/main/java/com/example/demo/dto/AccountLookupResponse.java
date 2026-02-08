package com.example.demo.dto;

import com.example.demo.model.Currency;

public class AccountLookupResponse {
    private String accountNumber;
    private String accountName;
    private Currency currency;

    public AccountLookupResponse(String accountNumber, String accountName, Currency currency) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.currency = currency;
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
}
