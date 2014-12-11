package com.dendreon.intellivenge.dataservice.config;

import static com.google.inject.jndi.JndiIntegration.fromJndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.dendreon.intellivenge.dataservice.DataService;
import com.dendreon.intellivenge.dataservice.JDBCDataService;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

public class JDBCDataServiceModule extends PrivateModule {

	@Override
	protected void configure() {
		
		bind(Context.class).to(InitialContext.class);
		
		bind(DataSource.class).annotatedWith(Names.named("XxsapJndi"))
		.toProvider(
				fromJndi(DataSource.class,
						"java:/comp/env/jdbc/xxsap"));
		expose(DataSource.class).annotatedWith(Names.named("XxsapJndi"));
		
		bind(DataService.class).to(JDBCDataService.class);
		expose(DataService.class);
	}
}
