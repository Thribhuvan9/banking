package com.thl.banking;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thl.banking.Transformer.GsonTransformer;
import com.thl.banking.config.component.ApplicationComponent;
import com.thl.banking.config.component.DaggerApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Application {

    private static ApplicationComponent applicationComponent;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private void start() {
        logger.info("Initializing Dagger--->>");
        initializeDagger();
        registerRoutes();
        logger.info("Initialized Successfully");

    }

    private void initializeDagger() {
        applicationComponent = DaggerApplicationComponent.create();
    }

    private void registerRoutes() {
        applicationComponent.resourceRegistry().registerRoutes();
    }

    public static void main(String[] args) {
        new Application().start();
    }
}
