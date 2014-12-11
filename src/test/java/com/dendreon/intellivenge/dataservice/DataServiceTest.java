package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
	public void findRecordTest() throws SQLException, ParseException {

		QueryParameter q = new QueryParameter("id", QueryType.EQ, new Integer(101397));
		ResultSet recordSet = service.findRecords("doctor", q);
		recordSet.next();
		int id = recordSet.getInt("id");
		assertEquals(id, 101397, "id is same");
		
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Date date = format.parse("01/02/1950");
		q = new QueryParameter("date_of_birth", QueryType.LT, date);
		recordSet = service.findRecords("patient", q);
		recordSet.next();
		id = recordSet.getInt("id");
		assertEquals(id, 1234, "id is same");
	}

}
