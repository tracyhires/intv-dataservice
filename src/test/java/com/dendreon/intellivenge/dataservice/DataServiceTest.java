package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.dendreon.intellivenge.dataservice.config.TestDataServiceModule;

@Guice(modules = {TestDataServiceModule.class})
public class DataServiceTest {
	
	@Inject
	DataService service;
	
	@Test
	public void findRecordTest() throws SQLException {

		QueryParameter q = new QueryParameter("id", QueryType.EQ, new Integer(101397));
		ResultSet recordSet = service.findRecords("doctor", q);
		int id = recordSet.getInt("id");
		assertEquals(id, 101397);
	}

}
