package com.dendreon.intellivenge.dataservice.config;

import com.dendreon.guicepersist.AbstractPersistService;
import com.dendreon.guicepersist.StaticPersistServiceListProvider;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import java.util.List;

public class TestDataServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<List<AbstractPersistService>>() {})
                .toProvider(StaticPersistServiceListProvider.class);

        install(new JndiDependenciesITModule());
        install(new JDBCDataServiceModule());
    }
}
