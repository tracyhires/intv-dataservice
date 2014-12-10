package com.dendreon.intellivenge.dataservice.config;

import java.util.Properties;
import java.util.Set;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class JndiDependenciesITModule extends AbstractModule {

    @Override
    protected void configure() {
        bindNamedPropsFromSysProps();
    }

    private void bindNamedPropsFromSysProps() {
        Properties dependencies = new Properties();
        Properties sysprop = System.getProperties();
        Set<String> keys = sysprop.stringPropertyNames();
        for (String key : keys) {
            if (key.startsWith("dependency.")) {
                dependencies.put(key, sysprop.get(key));
            }
        }
        Names.bindProperties(binder(), dependencies);
    }
}
