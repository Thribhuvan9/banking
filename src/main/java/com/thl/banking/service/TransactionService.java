package com.thl.banking.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thl.banking.exception.InsufficientFundException;
import com.thl.banking.model.Account;
import com.thl.banking.model.ErrorResponse;
import com.thl.banking.model.Transaction;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.*;

@Singleton
public class TransactionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Gson gson;
    TransactionDAO transactionDAO;
    AccountDAO accountDAO;


    @Inject
    public TransactionService(Gson gson, TransactionDAO transactionDAO, AccountDAO accountDAO) {
        this.gson = gson;
        this.transactionDAO = transactionDAO;
        this.accountDAO = accountDAO;
    }

    public Object getAllTransaction(Request request, Response response) {
        logger.info("Request received to get all Transactions");
        response.type("application/json");

        Queue<Transaction> transactions;
        try {
            transactions = transactionDAO.getAllTransactions();
            response.status(200);
        } catch (Exception e) {
            logger.error("Exception occured while getting transactions: " + e.getMessage());
            response.status(500);
            return new ErrorResponse(500, "INTERNAL_SERVER_ERROR");
        }
        if (transactions == null || transactions.isEmpty()) {
            logger.info("No transaction found");
            response.status(404);
            return new ErrorResponse(404, "NO_TRANSACTIONS_FOUND");
        }
        return transactions;
    }

    public Object getTransactionById(Request request, Response response) {
        logger.info("Request received to get one Transaction by id");
        response.type("application/json");
        String id = String.valueOf(request.params(":id"));
        Optional<Transaction> transaction;
        try {
            transaction = transactionDAO.getTransactionById(id);
            response.status(200);
        } catch (Exception e) {
            logger.error("Exception occured while transaction: " + e.getMessage());
            response.status(500);
            return new ErrorResponse(500, "INTERNAL_SERVER_ERROR");
        }

        if (!transaction.isPresent()) {
            logger.info("No transaction found for id: " + id);
            response.status(404);
            return new ErrorResponse(404, "NOT_FOUND");
        }
        return transaction.get();
    }

    public Object transfer(Request request, Response response) {
        logger.info("Request received for new Transaction");
        response.type("application/json");
        JsonObject requestJson = gson.fromJson(request.body(), JsonObject.class);
        String from;
        String to;
        BigDecimal amount;
        String currency;
        try {
            from = requestJson.get("sender").getAsString();
            to = requestJson.get("receiver").getAsString();
            amount = requestJson.get("amount").getAsBigDecimal();
            currency = requestJson.get("currency").getAsString();
        } catch (Exception e) {
            logger.error("Invalid input: " + e.getMessage());
            response.status(400);
            return new ErrorResponse(400, "BAD_REQUEST");
        }
        Optional<Account> senderAccount = accountDAO.getAccountByAccountNumber(from);
        Optional<Account> receiverAccount = accountDAO.getAccountByAccountNumber(to);
        if (!senderAccount.isPresent() || !receiverAccount.isPresent()) {
            logger.error("sender or receiver account not present");
            response.status(404);
            return new ErrorResponse(404, "ACCOUNT_NOT_FOUND");
        }
        Transaction transaction = new Transaction(senderAccount.get(), receiverAccount.get(), amount, currency);
        try {
            transaction.setId(UUID.randomUUID().toString());
            transaction = transactionDAO.commit(transaction);
            response.status(200);
        } catch (InsufficientFundException e) {
            response.status(406);
            return new ErrorResponse(406, "INSUFFICIENT_FUND");
        } catch (InterruptedException e) {
            response.status(408);
            return new ErrorResponse(408, "REQUEST_TIMEOUT");
        } catch (Exception e) {
            logger.error("error: " + e.getMessage());
            response.status(500);
            return new ErrorResponse(500, "ERROR:" +
                    e.getClass().getName().toUpperCase() + ":" + e.getMessage());
        }
        return transaction;
    }
}
