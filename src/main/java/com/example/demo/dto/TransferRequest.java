package com.example.demo.dto;

import com.example.demo.model.Currency;
import java.math.BigDecimal;

public class TransferRequest {
    private Long sourceAccountId;
    private String targetAccountNumber;
    private String targetBankCode;
    private BigDecimal amount;
    private Currency currency;
    private String reference;

    // Getters and Setters
    public Long getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(Long sourceAccountId) { this.sourceAccountId = sourceAccountId; }

    public String getTargetAccountNumber() { return targetAccountNumber; }
    public void setTargetAccountNumber(String targetAccountNumber) { this.targetAccountNumber = targetAccountNumber; }

    public String getTargetBankCode() { return targetBankCode; }
    public void setTargetBankCode(String targetBankCode) { this.targetBankCode = targetBankCode; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
