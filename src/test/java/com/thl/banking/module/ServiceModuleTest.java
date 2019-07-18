package com.thl.banking.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thl.banking.config.module.ServiceModule;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.service.AccountService;
import com.thl.banking.service.TransactionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceModuleTest {


    @Mock
    private AccountDAO accountDAO;

    @Mock
    private TransactionDAO transactionDAO;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    ServiceModule serviceModule = new ServiceModule();

    @Test
    public void accountServiceTest() {
        AccountService service = serviceModule.provideAccountService(gson, accountDAO);
        Assert.assertNotNull(service);
    }

    @Test
    public void transactionServiceTest() {
        TransactionService service = serviceModule.provideTransactionService(gson, transactionDAO, accountDAO);
        Assert.assertNotNull(service);
    }

    @Test
    public void gsonTest() {
        Gson gson = serviceModule.provideGson();
        Assert.assertNotNull(gson);
    }

}
