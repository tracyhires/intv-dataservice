package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.dendreon.intellivenge.dataservice.config.TestDataServiceModule;
import com.dendreon.test.db.config.JndiDbModule;

@Guice(modules = {TestDataServiceModule.class, JndiDbModule.class})
public class DataServiceTest {
	
	@Inject
	DataService service;
	
	@Test
	public void findRecordTest() throws SQLException {

		QueryParameter q = new QueryParameter("id", QueryType.EQ, new Integer(101397));
		ResultSet recordSet = service.findRecords("doctor", q);
		recordSet.next();
		int id = recordSet.getInt("id");
		assertEquals(id, 101397, "id is same");
	}

}
