package com.thl.banking.repository.inmemory;

import com.thl.banking.exception.AccountAlreadyExistsException;
import com.thl.banking.exception.CurrentcyMismatchException;
import com.thl.banking.exception.InvalidAccountException;
import com.thl.banking.model.Account;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.validator.AccountValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountDAOImpl implements AccountDAO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, Account> allAccounts = new HashMap<>();
    private AccountValidation accountValidation;

    @Inject
    public InMemoryAccountDAOImpl(AccountValidation accountValidation) {
        this.accountValidation = accountValidation;
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountId) {
        Account account = allAccounts.get(accountId);
        if (account == null) {
            return Optional.empty();
        }
        return Optional.of(account);
    }

    @Override
    public Map<String, Account> getAllAccounts() {
        return allAccounts;
    }

    @Override
    public Account create(Account account) throws Exception {
        if (allAccounts.containsKey(account.getAccountId())) {
            throw new AccountAlreadyExistsException(account.getAccountId());
        }
        Exception error = accountValidation.validate(account);
        account.setCreatedTime(LocalDateTime.now().toString());
        account.setUpdatedTime(LocalDateTime.now().toString());
        if (error != null) {
            throw error;
        }
        allAccounts.put(account.getAccountId(), account);
        return account;

    }

    @Override
    public Account updateByAccountNumber(String accountId, Account account) throws Exception {
        if (!allAccounts.containsKey(accountId)) {
            throw new InvalidAccountException(String.format("account with number %s does not exist", accountId));
        }

        Exception error = accountValidation.validate(account);

        if (error != null) {
            throw error;
        }
        Account updatedAccount = new Account();
        updatedAccount.setAccountId(accountId);
        updatedAccount.setUser(account.getUser());
        updatedAccount.setBalance(account.getBalance());
        updatedAccount.setCurrencyUnit(account.getCurrencyUnit());
        updatedAccount.setUpdatedTime(LocalDateTime.now().toString());

        return account;
    }

    @Override
    public void withdrawMoney(Account account, BigDecimal amount, String currencyUnit) {
        if (!allAccounts.containsKey(account.getAccountId())) {
            throw new InvalidAccountException(String.format("account with number %s does not exist", account.getAccountId()));
        }
        if (!currencyUnit.equals(account.getCurrencyUnit())) {
            throw new CurrentcyMismatchException();
        }

        account.setBalance(account.getBalance().subtract(amount));
        allAccounts.put(account.getAccountId(), account);
    }

    @Override
    public void depositMoney(Account account, BigDecimal amount, String currencyUnit) {
        if (!allAccounts.containsKey(account.getAccountId())) {
            throw new InvalidAccountException(String.format("account with number %s does not exist", account.getAccountId()));
        }
        if (!currencyUnit.equals(account.getCurrencyUnit())) {
            throw new CurrentcyMismatchException();
        }
        account.setBalance(account.getBalance().add(amount));
        allAccounts.put(account.getAccountId(), account);
    }

    @Override
    public void deleteAccount(String accountId) {
        if (!allAccounts.containsKey(accountId)) {
            throw new InvalidAccountException(String.format("account with number %s does not exist", accountId));
        }

        allAccounts.remove(accountId);
    }

    @Override
    public void clear() {
        allAccounts.clear();
    }
}
