package com.dendreon.intellivenge.dataservice.config;


import com.dendreon.intellivenge.dataservice.DataService;
import com.dendreon.intellivenge.dataservice.JDBCDataService;
import com.google.inject.PrivateModule;

public class JDBCDataServiceModule extends PrivateModule {

	@Override
	protected void configure() {
		bind(DataService.class);
		expose(JDBCDataService.class);
	}
}
