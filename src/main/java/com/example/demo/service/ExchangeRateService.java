package com.example.demo.service;

import com.example.demo.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final Map<String, BigDecimal> rates = new HashMap<>();

    public ExchangeRateService() {
        // Base Currency: USD
        rates.put("USD", BigDecimal.ONE);
        rates.put("EUR", new BigDecimal("0.92")); // 1 USD = 0.92 EUR
        rates.put("GBP", new BigDecimal("0.79")); // 1 USD = 0.79 GBP
        rates.put("JPY", new BigDecimal("148.50")); // 1 USD = 148.50 JPY
        rates.put("AUD", new BigDecimal("1.53"));
        rates.put("CAD", new BigDecimal("1.35"));
        rates.put("CHF", new BigDecimal("0.87"));
    }

    public BigDecimal getRate(Currency from, Currency to) {
        if (from == to) return BigDecimal.ONE;

        BigDecimal fromRate = rates.get(from.name());
        BigDecimal toRate = rates.get(to.name());

        if (fromRate == null || toRate == null) {
            throw new RuntimeException("Exchange rate not available for " + from + " to " + to);
        }

        // Convert to USD then to Target
        // AmountInUSD = AmountInFrom / FromRate
        // AmountInTo = AmountInUSD * ToRate
        // Rate = ToRate / FromRate
        return toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
    }

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        BigDecimal rate = getRate(from, to);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
