package com.example.demo.service;

import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.AccountLookupResponse;
import com.example.demo.model.*;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ExchangeRateService exchangeRateService;

    public BankingService(AccountRepository accountRepository, 
                          TransactionRepository transactionRepository, 
                          UserRepository userRepository,
                          ExchangeRateService exchangeRateService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.exchangeRateService = exchangeRateService;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usernameOrEmail;
        if (principal instanceof UserDetails) {
            usernameOrEmail = ((UserDetails) principal).getUsername();
        } else {
            usernameOrEmail = principal.toString();
        }
        // Since we use email as the username in UserDetails (see UserDetailServiceImpl), we should search by email first.
        // Fallback to username if needed, or just assume it's email if that's our convention.
        return userRepository.findByEmail(usernameOrEmail)
                .or(() -> userRepository.findByUsername(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = getCurrentUser();
        return createAccountForUser(user, request.getCurrency());
    }

    public void createDefaultAccounts(User user) {
        for (Currency currency : Currency.values()) {
            createAccountForUser(user, currency);
        }
    }

    private AccountResponse createAccountForUser(User user, Currency currency) {
        Account account = new Account();
        account.setUser(user);
        account.setCurrency(currency);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        
        // Generate random account number and IBAN (Mock)
        String accNum = String.valueOf((long) (Math.random() * 1000000000L));
        account.setAccountNumber(accNum);
        account.setIban("IBAN" + accNum); // Simplified IBAN

        account = accountRepository.save(account);

        return mapToResponse(account);
    }

    public List<AccountResponse> getMyAccounts() {
        User user = getCurrentUser();
        return accountRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<Transaction> getAccountTransactions(Long accountId) {
        User user = getCurrentUser();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account transactions");
        }

        return transactionRepository.findBySourceAccountId(accountId);
    }

    @Transactional
    public AccountResponse deposit(DepositRequest request) {
        User user = getCurrentUser();
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        account = accountRepository.save(account);
        
        // Record Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(account.getCurrency());
        transaction.setSourceAccount(account); // In a real system, this might be a system account or "Cash Deposit"
        transaction.setTargetAccountNumber(account.getAccountNumber());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReference("Deposit");
        transactionRepository.save(transaction);

        return mapToResponse(account);
    }

    @Transactional
    public Transaction initiateTransfer(TransferRequest request) {
        User user = getCurrentUser();
        
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        if (!sourceAccount.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to source account");
        }

        // Check for sufficient funds
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccountNumber(request.getTargetAccountNumber());
        transaction.setTargetBankCode(request.getTargetBankCode());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setReference(request.getReference());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(java.time.LocalDateTime.now()); // Ensure this field exists in Entity or use @PrePersist

        // Calculate Fees
        BigDecimal fee = BigDecimal.ZERO;
        // Logic for internal transfer (same bank)
        // If targetBankCode is null or matches our bank code (assuming "MYBANK")
        if (request.getTargetBankCode() == null || "MYBANK".equals(request.getTargetBankCode())) {
            accountRepository.findByAccountNumber(request.getTargetAccountNumber()).ifPresentOrElse(targetAccount -> {
                // Determine if cross-currency
                BigDecimal transferFee = BigDecimal.ZERO;
                if (sourceAccount.getCurrency() != targetAccount.getCurrency()) {
                    // Cross-currency fee: 1%
                    transferFee = request.getAmount().multiply(new BigDecimal("0.01"));
                } else {
                    // Same-currency fee: 0 (or small flat fee)
                    transferFee = BigDecimal.ZERO; 
                }
                
                // Check total funds required (Amount + Fee)
                if (sourceAccount.getBalance().compareTo(request.getAmount().add(transferFee)) < 0) {
                     throw new RuntimeException("Insufficient funds to cover amount plus fee: " + transferFee);
                }

                // Perform transfer
                
                // Deduct from source (Amount + Fee)
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount().add(transferFee)));
                accountRepository.save(sourceAccount);

                // Add to target (with FX if needed)
                BigDecimal targetAmount = request.getAmount();
                if (sourceAccount.getCurrency() != targetAccount.getCurrency()) {
                     targetAmount = exchangeRateService.convert(request.getAmount(), sourceAccount.getCurrency(), targetAccount.getCurrency());
                }

                targetAccount.setBalance(targetAccount.getBalance().add(targetAmount));
                accountRepository.save(targetAccount);

                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setFee(transferFee);
            }, () -> {
                 // Target account not found
                 // In a real system, we might fail or mark as PENDING for manual review if it's external
                 // But here we treat it as internal failure if bank code is missing/internal
                 throw new RuntimeException("Target account not found for internal transfer");
            });
        }
        
        return transactionRepository.save(transaction);
    }

    public AccountLookupResponse performNameEnquiry(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String fullName = (account.getUser().getFirstName() != null ? account.getUser().getFirstName() : "") + 
                          " " + 
                          (account.getUser().getLastName() != null ? account.getUser().getLastName() : "");
        fullName = fullName.trim();
        if (fullName.isEmpty()) {
            fullName = account.getUser().getUsername();
        }

        return new AccountLookupResponse(account.getAccountNumber(), fullName, account.getCurrency());
    }

    private AccountResponse mapToResponse(Account account) {
        String fullName = (account.getUser().getFirstName() != null ? account.getUser().getFirstName() : "") + 
                          " " + 
                          (account.getUser().getLastName() != null ? account.getUser().getLastName() : "");
        fullName = fullName.trim();
        if (fullName.isEmpty()) {
            fullName = account.getUser().getUsername();
        }

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getIban(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                fullName
        );
    }
}
