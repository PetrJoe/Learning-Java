package com.example.demo.controller;

import com.example.demo.dto.AccountLookupResponse;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransferRequest;
import com.example.demo.model.Transaction;
import com.example.demo.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banking")
@Tag(name = "Banking", description = "Banking Operations")
@SecurityRequirement(name = "bearerAuth")
public class BankingController {

    private final BankingService bankingService;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping("/accounts")
    @Operation(summary = "Create a new account")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        return ResponseEntity.ok(bankingService.createAccount(request));
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get my accounts")
    public ResponseEntity<List<AccountResponse>> getMyAccounts() {
        return ResponseEntity.ok(bankingService.getMyAccounts());
    }

    @GetMapping("/accounts/{accountId}/transactions")
    @Operation(summary = "Get account transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long accountId) {
        return ResponseEntity.ok(bankingService.getAccountTransactions(accountId));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds (For testing)")
    public ResponseEntity<AccountResponse> deposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok(bankingService.deposit(request));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Initiate a transfer")
    public ResponseEntity<Transaction> initiateTransfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(bankingService.initiateTransfer(request));
    }

    @GetMapping("/name-enquiry")
    @Operation(summary = "Look up account holder name")
    public ResponseEntity<AccountLookupResponse> nameEnquiry(@RequestParam String accountNumber) {
        return ResponseEntity.ok(bankingService.performNameEnquiry(accountNumber));
    }
}
