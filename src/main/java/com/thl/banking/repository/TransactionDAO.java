package com.thl.banking.repository;

import com.thl.banking.model.Transaction;

import java.util.Optional;
import java.util.Queue;

public interface TransactionDAO {

    Optional<Transaction> getTransactionById(String id);

    Queue<Transaction> getAllTransactions();

    Transaction commit(Transaction transaction) throws Exception;

    void clear();
}
