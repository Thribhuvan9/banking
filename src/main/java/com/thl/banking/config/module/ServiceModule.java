package com.thl.banking.config.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.service.AccountService;
import com.thl.banking.service.TransactionService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Inject
    @Provides
    @Singleton
    public AccountService provideAccountService(final Gson gson, final AccountDAO accountDAO) {
        return new AccountService(gson, accountDAO);
    }

    @Inject
    @Provides
    @Singleton
    public TransactionService provideTransactionService(final Gson gson, final TransactionDAO transactionDAO, final AccountDAO accountDAO) {
        return new TransactionService(gson, transactionDAO, accountDAO);
    }
}
