package com.example.demo.dto;

import com.example.demo.model.Currency;

public class CreateAccountRequest {
    private Currency currency;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
