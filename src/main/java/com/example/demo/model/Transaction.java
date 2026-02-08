package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "transactions")
@Schema(description = "Financial Transaction entity")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique Transaction ID")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Transaction Amount")
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    @Schema(description = "Transaction Fee")
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Transaction Currency")
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @Column(name = "target_account_number")
    @Schema(description = "Target Account Number (Internal or External)")
    private String targetAccountNumber;

    @Column(name = "target_bank_code")
    @Schema(description = "SWIFT/BIC or Sort Code")
    private String targetBankCode; // SWIFT/BIC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Transaction Status")
    private TransactionStatus status;

    @Column
    @Schema(description = "Payment Reference/Description")
    private String reference;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = TransactionStatus.PENDING;
    }

    // Constructors
    public Transaction() {}

    public Transaction(BigDecimal amount, Currency currency, Account sourceAccount, String targetAccountNumber, String targetBankCode, String reference) {
        this.amount = amount;
        this.currency = currency;
        this.sourceAccount = sourceAccount;
        this.targetAccountNumber = targetAccountNumber;
        this.targetBankCode = targetBankCode;
        this.reference = reference;
        this.status = TransactionStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public Account getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }

    public String getTargetAccountNumber() { return targetAccountNumber; }
    public void setTargetAccountNumber(String targetAccountNumber) { this.targetAccountNumber = targetAccountNumber; }

    public String getTargetBankCode() { return targetBankCode; }
    public void setTargetBankCode(String targetBankCode) { this.targetBankCode = targetBankCode; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
