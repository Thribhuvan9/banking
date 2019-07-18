package com.thl.banking.repository;

import com.thl.banking.model.Account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface AccountDAO {

    Optional<Account> getAccountByAccountNumber(String accountId);

    Map<String, Account> getAllAccounts();

    Account create(Account account) throws Exception;

    Account updateByAccountNumber(String accountId, Account account) throws Exception;

    void withdrawMoney(Account account, BigDecimal amount, String currencyUnit);

    void depositMoney(Account account, BigDecimal amount, String currencyUnit);

    void deleteAccount(String accountId);

    void clear();
}
