package com.thl.banking.module;

import com.thl.banking.config.module.ValidationModule;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.validator.AccountValidation;
import com.thl.banking.validator.TransactionValidation;
import com.thl.banking.validator.impl.AccountValidationImpl;
import com.thl.banking.validator.impl.TransactionValidationImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ValidationModuleTest {

    private ValidationModule validationModule = new ValidationModule();

    @Mock
    private AccountDAO accountDAO;

    @Test
    public void transactionValidationTest() {
        TransactionValidation validation = validationModule.provideTransactionValidation(accountDAO);
        assertThat(validation).isNotNull();
        assertThat(validation).isInstanceOf(TransactionValidationImpl.class);
    }

    @Test
    public void accountValidationTest() {
        AccountValidation validation = validationModule.provideAccountValidation();
        assertThat(validation).isNotNull();
        assertThat(validation).isInstanceOf(AccountValidationImpl.class);
    }
}
