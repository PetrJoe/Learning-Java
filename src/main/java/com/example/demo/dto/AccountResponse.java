package com.example.demo.dto;

import com.example.demo.model.AccountStatus;
import com.example.demo.model.Currency;
import java.math.BigDecimal;

public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String iban;
    private Currency currency;
    private BigDecimal balance;
    private AccountStatus status;
    private String accountHolderName;

    public AccountResponse(Long id, String accountNumber, String iban, Currency currency, BigDecimal balance, AccountStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.iban = iban;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
    }

    public AccountResponse(Long id, String accountNumber, String iban, Currency currency, BigDecimal balance, AccountStatus status, String accountHolderName) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.iban = iban;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.accountHolderName = accountHolderName;
    }

    // Getters
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getIban() { return iban; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public String getAccountHolderName() { return accountHolderName; }
}
