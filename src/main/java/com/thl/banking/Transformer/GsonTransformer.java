package com.thl.banking.Transformer;

import com.google.gson.Gson;
import spark.ResponseTransformer;
import javax.inject.Singleton;

@Singleton
public class GsonTransformer implements ResponseTransformer {

    private final Gson gson;

    public GsonTransformer(Gson gson){
        this.gson = gson;
    }

    @Override
    public String render(Object model) throws Exception {
        return gson.toJson(model);
    }
}