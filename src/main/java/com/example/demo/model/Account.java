package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "accounts")
@Schema(description = "Bank Account entity")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique Account ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Account Number")
    private String accountNumber;

    @Column(unique = true)
    @Schema(description = "International Bank Account Number")
    private String iban;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Currency code")
    private Currency currency;

    @Column(nullable = false)
    @Schema(description = "Current Balance")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Account Status")
    private AccountStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (balance == null) balance = BigDecimal.ZERO;
        if (status == null) status = AccountStatus.ACTIVE;
    }

    // Constructors
    public Account() {}

    public Account(String accountNumber, Currency currency, User user) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
