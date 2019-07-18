package com.thl.banking.config.module;

import com.thl.banking.repository.AccountDAO;
import com.thl.banking.validator.AccountValidation;
import com.thl.banking.validator.TransactionValidation;
import com.thl.banking.validator.impl.AccountValidationImpl;
import com.thl.banking.validator.impl.TransactionValidationImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module
public class ValidationModule {


    @Inject
    @Provides
    @Singleton
    public AccountValidation provideAccountValidation() {
        return new AccountValidationImpl();
    }

    @Inject
    @Provides
    @Singleton
    public TransactionValidation provideTransactionValidation(final AccountDAO accountDAO) {
        return new TransactionValidationImpl(accountDAO);
    }


}
