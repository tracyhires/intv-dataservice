package com.dendreon.intellivenge.dataservice;

import com.dendreon.intellivenge.dataservice.config.JDBCDataServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DataServiceFactory {
	
	public DataService createDataService() {
		Injector injector = Guice.createInjector(new JDBCDataServiceModule());
		return injector.getInstance(DataService.class);
	}
}
