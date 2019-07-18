package com.thl.banking.module;

import com.thl.banking.config.module.DatabaseModule;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.repository.inmemory.InMemoryAccountDAOImpl;
import com.thl.banking.repository.inmemory.InMemoryTransactionDAOImpl;
import com.thl.banking.validator.AccountValidation;
import com.thl.banking.validator.TransactionValidation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseModuleTest {
    private DatabaseModule databaseModule = new DatabaseModule();

    @Mock
    private AccountDAO accountDAO;
    @Mock
    private AccountValidation accountValidation;
    @Mock
    private TransactionValidation transactionValidation;

    @Test
    public void accountDAOTest() {
        AccountDAO accountRepository = databaseModule.provideAccountDAO(accountValidation);
        Assert.assertNotNull(accountRepository);
        assertThat(accountRepository).isNotNull();
        assertThat(accountRepository).isInstanceOf(InMemoryAccountDAOImpl.class);
    }

    @Test
    public void transactionDAOTest() {
        TransactionDAO transactionRepository = databaseModule.provideTransactionDAO(accountDAO, transactionValidation);
        assertThat(transactionRepository).isNotNull();
        assertThat(transactionRepository).isInstanceOf(InMemoryTransactionDAOImpl.class);
    }
}
