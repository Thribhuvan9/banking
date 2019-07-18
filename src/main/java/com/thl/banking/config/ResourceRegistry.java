package com.thl.banking.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thl.banking.Transformer.GsonTransformer;
import com.thl.banking.service.AccountService;
import com.thl.banking.service.TransactionService;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ResponseTransformer;

import javax.inject.Inject;
import javax.inject.Singleton;


import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

@Singleton
public class ResourceRegistry {

    private ResponseTransformer responseTransformer;
    private AccountService accountService;
    private TransactionService transactionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public ResourceRegistry(AccountService accountService, TransactionService transactionService) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.responseTransformer = new GsonTransformer(gson);

    }

    public void registerRoutes() {
        logger.info("Staring Banking App. Initilizing serivce");
        //APIs
        get("/healthcheck", "application/json", accountService::test, responseTransformer);

        get("/account", "application/json", accountService::getAllAccounts, responseTransformer);
        post("/account", "application/json", accountService::addAccount, responseTransformer);
        get("/account/:id", "application/json", accountService::getByAccountId, responseTransformer);
        delete("/account/:id", "application/json", accountService::deleteAccount, responseTransformer);

        get("/transaction", "application/json", transactionService::getAllTransaction, responseTransformer);
        get("/transaction/:id", "application/json", transactionService::getTransactionById, responseTransformer);
        post("/transaction", "application/json", transactionService::transfer, responseTransformer);

    }


}
