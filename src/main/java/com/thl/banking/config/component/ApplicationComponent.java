package com.thl.banking.config.component;

import javax.inject.Singleton;

import com.thl.banking.config.ResourceRegistry;
import com.thl.banking.config.module.DatabaseModule;
import com.thl.banking.config.module.ServiceModule;
import com.thl.banking.config.module.ValidationModule;
import dagger.Component;

@Singleton
@Component(modules = {ServiceModule.class, ValidationModule.class, DatabaseModule.class})
public interface ApplicationComponent {
    ResourceRegistry resourceRegistry();
}
