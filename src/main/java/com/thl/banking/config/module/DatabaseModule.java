package com.thl.banking.config.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.thl.banking.Transformer.GsonTransformer;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.repository.inmemory.InMemoryAccountDAOImpl;
import com.thl.banking.repository.inmemory.InMemoryTransactionDAOImpl;
import com.thl.banking.service.AccountService;
import com.thl.banking.service.TransactionService;
import com.thl.banking.validator.AccountValidation;
import com.thl.banking.validator.TransactionValidation;
import dagger.Module;
import dagger.Provides;
import spark.ResponseTransformer;

@Module
public class DatabaseModule {

    @Inject
    @Provides
    @Singleton
    public AccountDAO provideAccountDAO(final AccountValidation accountValidation) {
        return new InMemoryAccountDAOImpl(accountValidation);
    }

@Inject
    @Provides
    @Singleton
    public TransactionDAO provideTransactionDAO(final AccountDAO accountDAO, final TransactionValidation transactionValidation) {
        return new InMemoryTransactionDAOImpl(accountDAO, transactionValidation);
    }


}

