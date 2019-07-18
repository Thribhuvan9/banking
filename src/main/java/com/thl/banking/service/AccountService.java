package com.thl.banking.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thl.banking.model.Account;
import com.thl.banking.model.ErrorResponse;
import com.thl.banking.repository.AccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class AccountService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    Gson gson;
    AccountDAO accountDAO;

    @Inject
    public AccountService(Gson gson, AccountDAO accountDAO) {
        this.gson = gson;
        this.accountDAO = accountDAO;
    }


    public String test(Request request, Response response) {
        JsonObject res = new JsonObject();
        res.addProperty("status", 200);
        res.addProperty("message", "Healthy");
        return gson.toJson(res);
    }

    public Object getAllAccounts(Request request, Response response) {
        logger.info("Request received to get ALL user accounts");
        response.type("application/json");
        Map<String, Account> accounts;
        try {
            accounts = accountDAO.getAllAccounts();
            response.status(200);
        } catch (Exception e) {
            logger.error("Exception occured while adding account: " + e.getMessage());
            response.status(500);
            return new ErrorResponse(500,  "INTERNAL_SERVER_ERROR");
        }
        if(accounts == null || accounts.isEmpty()){
            response.status(404);
            return new ErrorResponse(404,  "NO_ACCOUNT_FOUND");
        }
        return accounts;
    }

    public Object addAccount(Request request, Response response) {
        logger.info("Request received to add user accounts");
        response.type("application/json");
        Account account = gson.fromJson(request.body(), Account.class);
        try {
            account.setAccountId(UUID.randomUUID().toString());
            account = accountDAO.create(account);
            response.status(201);
        } catch (Exception e) {
            logger.error("Exception occured while adding account: " + e.getMessage());
            response.status(500);
            return new ErrorResponse(500,  "INTERNAL_SERVER_ERROR");
        }
        return account;
    }

    public Object getByAccountId(Request request, Response response) {
        logger.info("Request received to get single user accounts");
        response.type("application/json");
        String id = String.valueOf(request.params(":id"));
        Optional<Account> account;
        try {
            account = accountDAO.getAccountByAccountNumber(id);
            response.status(200);
        } catch (Exception e) {
            logger.error("Exception occured while adding account: " + e.getMessage());
            response.status(500);
            return new ErrorResponse(500, "INTERNAL_SERVER_ERROR");
        }
        if (!account.isPresent()) {
            logger.info("No account found for id: "+id);
            response.status(404);
            return new ErrorResponse(404, "NOT_FOUND");
        }
        return account.get();
    }

    public Object deleteAccount(Request request, Response response) {
        logger.info("Request received to delete user accounts");
        response.type("application/json");
        String id = String.valueOf(request.params(":id"));
        Optional<Account> account;
        try {
            accountDAO.deleteAccount(id);
            response.status(202);
        } catch (Exception e) {
            logger.error("Exception occured while adding account: " + e.getMessage());
            response.status(404);
            return new ErrorResponse(404, "NOT_FOUND");
        }
        JsonObject res = new JsonObject();
        res.addProperty("status", 202);
        res.addProperty("message", "success");
        return gson.toJson(res);
    }


}
